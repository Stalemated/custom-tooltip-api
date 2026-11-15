import tempfile
import unittest
import zipfile
from pathlib import Path

import check_ranges

SETTINGS = '''
stonecutter {
    create(rootProject) {
        fun match(project: String, vararg loaders: String, version: String = project) {}

        match("1.20.1", "fabric")
        match("1.21.1", "fabric")
        match("26.1", "fabric")
    }
}
'''

PROPERTIES = '''
mod.id = "my_mod"
publish.nodes = ["1.20.1-fabric", "26.1-fabric"]

["1.20.1"]
mod.mc_releases = ["1.20", "1.20.1"]

[fabric."1.20.1"]
mod.mc_releases = ["1.20", "1.20.1", "1.21", "1.21.1"]

["1.21.1"]
mod.mc_releases = ["1.21", "1.21.1"]

["26.1"]
mod.mc_releases = ["26.1"]
'''

JAVA_MAJORS = {17: 61, 21: 65, 25: 69}


def class_file(java, body):
    return b'\xca\xfe\xba\xbe\x00\x00' + JAVA_MAJORS[java].to_bytes(2, 'big') + body


def write_tree(root, properties, jars):
    (root / 'settings.gradle.kts').write_text(SETTINGS, encoding='utf-8')
    (root / 'stonecutter.properties.toml').write_text(properties, encoding='utf-8')
    for node, (java, body) in jars.items():
        libs = root / 'versions' / node / 'build' / 'libs'
        libs.mkdir(parents=True)
        with zipfile.ZipFile(libs / f'my-mod-{node}.jar', 'w') as archive:
            archive.writestr('com/example/MyMod.class', class_file(java, body))
            archive.writestr('fabric.mod.json', node)


class CheckRangesTest(unittest.TestCase):
    def problems_for(self, properties, jars):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            write_tree(root, properties, jars)
            return check_ranges.range_problems(root)

    def test_a_range_whose_classes_only_differ_in_java_version_passes(self):
        jars = {'1.20.1-fabric': (17, b'same'), '1.21.1-fabric': (21, b'same'), '26.1-fabric': (25, b'official')}

        self.assertEqual([], self.problems_for(PROPERTIES, jars))

    def test_a_lambda_numbered_differently_by_another_javac_still_matches(self):
        jars = {'1.20.1-fabric': (17, b'invoke lambda$check$1'), '1.21.1-fabric': (21, b'invoke lambda$check$0'),
                '26.1-fabric': (25, b'official')}

        self.assertEqual([], self.problems_for(PROPERTIES, jars))

    def test_a_class_that_differs_inside_a_range_is_reported(self):
        jars = {'1.20.1-fabric': (17, b'same'), '1.21.1-fabric': (21, b'changed'), '26.1-fabric': (25, b'official')}

        problems = self.problems_for(PROPERTIES, jars)

        self.assertEqual(1, len(problems))
        self.assertIn('1.21.1-fabric has different classes than 1.20.1-fabric', problems[0])

    def test_a_published_jar_newer_than_a_covered_node_is_reported(self):
        jars = {'1.20.1-fabric': (17, b'same'), '1.21.1-fabric': (21, b'same'), '26.1-fabric': (25, b'official')}
        properties = PROPERTIES.replace('["1.20.1-fabric", "26.1-fabric"]', '["1.21.1-fabric", "26.1-fabric"]').replace(
            'mod.mc_releases = ["1.21", "1.21.1"]', 'mod.mc_releases = ["1.20", "1.20.1", "1.21", "1.21.1"]')

        problems = self.problems_for(properties, jars)

        self.assertEqual(['1.21.1-fabric targets Java 21 but covers 1.20.1-fabric, which runs on Java 17'], problems)

    def test_a_node_outside_every_published_range_is_reported(self):
        jars = {'1.20.1-fabric': (17, b'same'), '1.21.1-fabric': (21, b'same'), '26.1-fabric': (25, b'official')}
        properties = PROPERTIES.replace('["1.20.1-fabric", "26.1-fabric"]', '["1.20.1-fabric"]')

        problems = self.problems_for(properties, jars)

        self.assertEqual(['26.1-fabric (26.1) must be covered by one published node, got []'], problems)

    def test_without_publish_nodes_there_is_nothing_to_compare(self):
        properties = PROPERTIES.replace('publish.nodes = ["1.20.1-fabric", "26.1-fabric"]\n', '')

        self.assertIsNone(self.problems_for(properties, {}))


if __name__ == '__main__':
    unittest.main()
