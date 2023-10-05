package rw.notification;

public enum RwNotificationType {
    GENERIC,
    GET_PRO;

    public static RwNotificationType fromString(String str) {
        switch(str) {
            case "Generic":
                return GENERIC;
            case "GetPro":
                return GET_PRO;
            default:
                return GENERIC;
        }
    }
}
