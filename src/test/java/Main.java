import com.denis.domain.exceptions.ControlException;
import com.denis.domain.exceptions.DAOException;
import com.denis.domain.exceptions.DomainException;
import com.denis.view.Login;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws ControlException {
        Logger logger = LogManager.getLogger();
        DAOException e0 = new DAOException("dao");
        DomainException e = new DomainException("lol", e0);
        ControlException e1 = new ControlException("username taken", e);
        logger.error(e1);
    }
}
