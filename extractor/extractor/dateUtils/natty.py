import jpype
import datetime

classpath = "res/javaLib.jar"
jpype.startJVM(jpype.getDefaultJVMPath(), "-Djava.class.path=%s" % classpath)

class Natty:
    
    def __init__(self):
        javaPackage = jpype.JPackage("org.aap.monitoring.natty")
        javaClass = javaPackage.NattyRunner
        self.nattyJavaObj = javaClass() 
    
    def extract_date(self, text):
        _FORMAT = '%Y-%m-%d'
        nattyDateFormat = '%a %b %d  %H:%M:%S UTC %Y'
        dateStr = self.nattyJavaObj.extractDate(text)
        try:
            dtObj = datetime.datetime.strptime(dateStr, nattyDateFormat)
            dtString = dtObj.strftime(_FORMAT)
            return dtString
        except:
            return dateStr
        return dateStr
