package com.java.node.database.double_write.doubleWrite;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;

import java.util.Arrays;
import java.util.List;

/**
 * @Description
 */
public class CCJSqlParserFunction {

    public static final List<CCJSqlParserInterface> list = Arrays.asList(
            new CCJSqlParserInterface<Insert>() {
                @Override
                public boolean isMatch(Statement statement) {
                    return statement instanceof Insert;
                }

                @Override
                public Insert parse(Statement statement) {
                    return (Insert) statement;
                }

                @Override
                public String getTableName(Insert statement) {
                    return statement.getTable().getName();
                }

                @Override
                public String getNewSql(Insert statement) {
                    Table table = statement.getTable();
                    table.setSchemaName(null);
                    return statement.toString();
                }
            },
            new CCJSqlParserInterface<Update>() {
                @Override
                public boolean isMatch(Statement statement) {
                    return statement instanceof Update;
                }

                @Override
                public Update parse(Statement statement) {
                    return (Update) statement;
                }

                @Override
                public String getTableName(Update statement) {
                    return statement.getTables().get(0).getName();
                }

                @Override
                public String getNewSql(Update statement) {
                    Table table = statement.getTables().get(0);
                    table.setSchemaName(null);
                    statement.setTables(Arrays.asList(table));
                    return statement.toString();
                }
            },
            new CCJSqlParserInterface<Delete>() {
                @Override
                public boolean isMatch(Statement statement) {
                    return statement instanceof Delete;
                }

                @Override
                public Delete parse(Statement statement) {
                    return (Delete) statement;
                }

                @Override
                public String getTableName(Delete statement) {
                    return statement.getTable().getName();
                }

                @Override
                public String getNewSql(Delete statement) {
                    Table table = statement.getTable();
                    table.setSchemaName(null);
                    return statement.toString();
                }
            }
    );

    public interface CCJSqlParserInterface<T extends Statement> {

        boolean isMatch(Statement statement);

        T parse(Statement statement);

        String getTableName(T statement);

        String getNewSql(T statement);

    }
}
