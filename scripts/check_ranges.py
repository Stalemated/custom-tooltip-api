import argparse
import hashlib
import re
import sys
import tomllib
import zipfile
from dataclasses import dataclass
from itertools import groupby
from pathlib import Path

MATCH_CALL = re.compile(r'^\s*match\(([^)]*)\)', re.MULTILINE)
QUOTED = re.compile(r'"([^"]+)"')
COMPARED_SUFFIXES = ('.class', '.refmap.json')
CLASS_FILE_JAVA_OFFSET = 44
LAMBDA_NUMBER = re.compile(rb'(lambda\$[A-Za-z0-9_]+\$)\d+')


@dataclass(frozen=True)
class Node:
    name: str
    loader: str
    releases: tuple[str, ...]


@dataclass(frozen=True)
class Fingerprint:
    digest: str
    java: int


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description='Check that every node is covered by a published node whose jar has the same classes. '
                    'Run ./gradlew clean build first.')
    parser.add_argument('--groups', action='store_true',
                        help='print runs of consecutive nodes with the same classes, to choose publish.nodes')
    return parser


def read_nodes(root: Path) -> list[Node]:
    settings = (root / 'settings.gradle.kts').read_text(encoding='utf-8')
    properties = read_properties(root)
    nodes = []
    for call in MATCH_CALL.finditer(settings):
        arguments = [QUOTED.fullmatch(part.strip()) for part in call.group(1).split(',')]
        version, *loaders = [argument.group(1) for argument in arguments if argument]
        nodes.extend(Node(f'{version}-{loader}', loader, releases_of(properties, version, loader)) for loader in loaders)
    return nodes


def read_properties(root: Path) -> dict:
    return tomllib.loads((root / 'stonecutter.properties.toml').read_text(encoding='utf-8'))


def releases_of(properties: dict, version: str, loader: str) -> tuple[str, ...]:
    for table in (properties.get(loader, {}).get(version, {}), properties.get(version, {})):
        releases = table.get('mod', {}).get('mc_releases')
        if releases:
            return tuple(releases)
    raise ValueError(f'mod.mc_releases is missing for node {version}-{loader}')


def built_jar(root: Path, node: Node) -> Path:
    libs = root / 'versions' / node.name / 'build' / 'libs'
    jars = sorted(path for path in libs.glob('*.jar') if not path.stem.endswith('-sources')) if libs.is_dir() else []
    if len(jars) != 1:
        raise ValueError(f'{node.name} must have one built jar in {libs}, found {len(jars)}: run ./gradlew clean build first')
    return jars[0]


def fingerprint(jar: Path) -> Fingerprint:
    digest = hashlib.sha256()
    java = 0
    with zipfile.ZipFile(jar) as archive:
        for name in sorted(archive.namelist()):
            if not name.endswith(COMPARED_SUFFIXES):
                continue
            content = archive.read(name)
            if name.endswith('.class'):
                java = max(java, int.from_bytes(content[6:8], 'big') - CLASS_FILE_JAVA_OFFSET)
                content = LAMBDA_NUMBER.sub(rb'\1', content[:4] + content[8:])
            digest.update(f'{name}:{len(content)}\n'.encode())
            digest.update(content)
    return Fingerprint(digest.hexdigest(), java)


def range_problems(root: Path) -> list[str] | None:
    published_names = read_properties(root).get('publish', {}).get('nodes')
    if published_names is None:
        return None
    nodes = read_nodes(root)
    unknown = sorted(set(published_names) - {node.name for node in nodes})
    if unknown:
        return [f'publish.nodes must only list existing nodes, got unknown {unknown}']

    published = [node for node in nodes if node.name in published_names]
    fingerprints = {node.name: fingerprint(built_jar(root, node)) for node in nodes}
    problems = []
    for node in nodes:
        if node in published:
            continue
        owners = [owner for owner in published if owner.loader == node.loader and set(node.releases) <= set(owner.releases)]
        if len(owners) != 1:
            names = [owner.name for owner in owners]
            problems.append(f'{node.name} ({", ".join(node.releases)}) must be covered by one published node, got {names}')
            continue
        owner = owners[0]
        if fingerprints[owner.name].digest != fingerprints[node.name].digest:
            problems.append(f'{node.name} has different classes than {owner.name}, so the {owner.name} jar may break on {", ".join(node.releases)}')
        if fingerprints[owner.name].java > fingerprints[node.name].java:
            problems.append(f'{owner.name} targets Java {fingerprints[owner.name].java} '
                            f'but covers {node.name}, which runs on Java {fingerprints[node.name].java}')
    return problems


def equivalent_runs(root: Path) -> list[list[Node]]:
    nodes = read_nodes(root)
    fingerprints = {node.name: fingerprint(built_jar(root, node)) for node in nodes}
    runs = []
    for loader in dict.fromkeys(node.loader for node in nodes):
        loader_nodes = [node for node in nodes if node.loader == loader]
        runs.extend(list(run) for _, run in groupby(loader_nodes, key=lambda node: fingerprints[node.name].digest))
    return runs


def main(argv: list[str]) -> int:
    args = build_parser().parse_args(argv)
    root = Path(__file__).resolve().parent.parent
    try:
        if args.groups:
            for run in equivalent_runs(root):
                first, last = run[0].releases[0], run[-1].releases[-1]
                span = first if first == last else f'{first}-{last}'
                print(f'{span} {run[0].loader}: publish {run[0].name}, covers {" ".join(node.name for node in run)}')
            return 0
        problems = range_problems(root)
    except ValueError as error:
        print(error, file=sys.stderr)
        return 1

    if problems is None:
        print('publish.nodes is not set, so every node publishes its own jar and there is nothing to compare.')
        return 0
    for problem in problems:
        print(problem, file=sys.stderr)
    print(f'{len(problems)} problems.' if problems else 'Every node is covered by a published jar with the same classes.')
    return 1 if problems else 0


if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
