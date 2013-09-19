import jpype

classpath = "res/javaLib.jar"
jpype.startJVM(jpype.getDefaultJVMPath(), "-Djava.class.path=%s" % classpath)

class Natty:
    
    def __init__(self):
        javaPackage = jpype.JPackage("org.aap.monitoring.natty")
        javaClass = javaPackage.NattyRunner
        self.nattyJavaObj = javaClass() 
    
    def extract_date(self, text):
        dateStr = self.nattyJavaObj.extractDate(text)
        return dateStr