package nro.noti;

import lombok.Getter;
import lombok.Setter;
import nro.consts.Cmd;
import nro.jdbc.DBService;
import nro.server.io.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
@Setter
@Getter
public class Notification {

    private int id;

    private String title;

    private String content;

}
