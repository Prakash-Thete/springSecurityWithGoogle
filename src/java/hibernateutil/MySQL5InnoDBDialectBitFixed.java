package hibernateutil;

/**
 * Created by
 */

import org.hibernate.dialect.MySQL5InnoDBDialect;

import java.sql.Types;

/**
 * Created by Administrator on 4/28/14.
 */

public class MySQL5InnoDBDialectBitFixed extends MySQL5InnoDBDialect {

    public MySQL5InnoDBDialectBitFixed() {
        super();
        registerColumnType(Types.BIT, "tinyint(1)");
    }
}


