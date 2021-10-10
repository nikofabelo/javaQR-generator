mkdir build dist
javac -cp lib -d build src/nikofabelo/javaqrgenerator/MainFrame.java
cp -r lib/* build && cp -r res build/nikofabelo/javaqrgenerator
jar cfm dist/javaQR-generator_v1.1.jar manifest.mf -C build .
