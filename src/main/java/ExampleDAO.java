import javax.swing.plaf.ColorUIResource;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Optional;

public class ExampleDAO {

    public static void main(String[] args) {

        JdbcCurrencyDao dao = new JdbcCurrencyDao();
        List<Currency> entity = dao.findAll();
        Optional<Currency> currency = dao.findById(2);

        System.out.println(entity);
        System.out.println(currency);
    }
}
