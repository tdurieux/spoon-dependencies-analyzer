java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ../VLille/src/main -f txt -l class > examples/vlille-DependencyAnalyser.c2c
java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ../VLille/src/main -f dot -l class > examples/vlille-DependencyAnalyser.c2c.gv
java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ../VLille/src/main -f txt -l package > examples/vlille-DependencyAnalyser.p2p
java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ../VLille/src/main -f dot -l package > examples/vlille-DependencyAnalyser.p2p.gv

java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ./src/main -f txt -l class > examples/dependencyAnalyser-DependencyAnalyser.c2c
java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ./src/main -f dot -l class > examples/dependencyAnalyser-DependencyAnalyser.c2c.gv
java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ./src/main -f txt -l package > examples/dependencyAnalyser-DependencyAnalyser.p2p
java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ./src/main -f dot -l package > examples/dependencyAnalyser-DependencyAnalyser.p2p.gv

java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ./src/testProject -f txt -l class > examples/testProject-DependencyAnalyser.c2c
java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ./src/testProject -f txt -l package > examples/testProject-DependencyAnalyser.p2p

java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ../spoon-core/src -f txt -l class > examples/spoon-DependencyAnalyser.c2c
java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ../spoon-core/src -f dot -l class > examples/spoon-DependencyAnalyser.c2c.gv
java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ../spoon-core/src -f txt -l package > examples/spoon-DependencyAnalyser.p2p
java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ../spoon-core/src -f dot -l package > examples/spoon-DependencyAnalyser.p2p.gv

java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ../nopol/nopol/src/ -f dot -l package > examples/nopol-DependencyAnalyzer.p2p.gv
java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ../nopol/nopol/src/ -f txt -l package > examples/nopol-DependencyAnalyzer.p2p
java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ../nopol/nopol/src/ -f dot -l class > examples/nopol-DependencyAnalyzer.p2p.gv
java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar ../nopol/nopol/src/ -f txt -l class > examples/nopol-DependencyAnalyzer.p2p