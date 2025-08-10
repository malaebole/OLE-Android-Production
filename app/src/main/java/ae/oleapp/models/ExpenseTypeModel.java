package ae.oleapp.models;

public class ExpenseTypeModel {
        private final int index;
        private final String value;

        public ExpenseTypeModel(int index, String value) {
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
