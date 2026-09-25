

public class CPPController {
    static {
        System.loadLibrary("nativecpp");
    }

    public CPPController() {
    }

    public native void syncSystemConstants();
    public native void executeCPPLogic();
    public native void executePrintHelloWorld();
}
