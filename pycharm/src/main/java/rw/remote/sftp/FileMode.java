package rw.remote.sftp;

public class FileMode {

    private final int mask;
    private final Type type;
    public FileMode(int mask) {
        this.mask = mask;
        this.type = Type.fromMask(getTypeMask());
    }

    public int getMask() {
        return mask;
    }

    public int getTypeMask() {
        return mask & 0170000;
    }

    public int getPermissionsMask() {
        return mask & 07777;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "[mask=" + Integer.toOctalString(mask) + "]";
    }

    public static enum Type {
        /**
         * block special
         */
        BLOCK_SPECIAL(0060000),
        /**
         * character special
         */
        CHAR_SPECIAL(0020000),
        /**
         * FIFO special
         */
        FIFO_SPECIAL(0010000),
        /**
         * socket special
         */
        SOCKET_SPECIAL(0140000),
        /**
         * regular
         */
        REGULAR(0100000),
        /**
         * directory
         */
        DIRECTORY(0040000),
        /**
         * symbolic link
         */
        SYMLINK(0120000),
        /**
         * unknown
         */
        UNKNOWN(0);

        private final int val;

        private Type(int val) {
            this.val = val;
        }

        public static Type fromMask(int mask) {
            for (Type t : Type.values())
                if (t.val == mask)
                    return t;
            return UNKNOWN;
        }

        public int toMask() {
            return val;
        }

    }

}