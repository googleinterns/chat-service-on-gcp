package Entity.Class;

public class ReturnObject {
    
    public int statusCode;
    public String statusReport; 
    public Object returnObject;

    public int statusCode(String statusReport) {
        switch (statusReport) {
            case "OK": return 0;
            case "Server Error": return 1;
            case "Username Exists": return 2;
            case "Username does not exist": return 3;
            case "Chat exists between given users": return 4;
        }
        return -1;
    }

    public String statusReport(int statusCode) {
        switch (statusCode) {
            case 0: return "OK";
            case 1: return "Server Error";
            case 2: return "Username Exists";
            case 3: return "Username does not exist";
            case 4: return "Chat exists between given users";
        }
        return "";
    }

    public ReturnObject(int statusCode, String statusReport, Object returnObject) {
        this.statusCode = statusCode;
        this.statusReport = statusReport;
        this.returnObject = returnObject;
    }

    public ReturnObject(int statusCode, Object returnObject) {
        this.statusCode = statusCode;
        this.statusReport = statusReport(statusCode);
        this.returnObject = returnObject;
    }

    public ReturnObject(String statusReport, Object returnObject) {
        this.statusCode = statusCode(statusReport);
        this.statusReport = statusReport;
        this.returnObject = returnObject;
    }

    public ReturnObject(int statusCode) {
        this.statusCode = statusCode;
        this.statusReport = statusReport(statusCode);
        this.returnObject = null;
    }

    public ReturnObject(String statusReport) {
        this.statusCode = statusCode(statusReport);
        this.statusReport = statusReport;
        this.returnObject = null;
    }
}