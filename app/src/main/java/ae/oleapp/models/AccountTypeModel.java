package ae.oleapp.models;

public class AccountTypeModel {

        private final int index;
        private final String value;

        public AccountTypeModel(int index, String value) {
        this.index = index;
        this.value = value;
    }

        public int getIndex() {
        return index;
    }

        public String getValue() {
        return value;
    }
}
