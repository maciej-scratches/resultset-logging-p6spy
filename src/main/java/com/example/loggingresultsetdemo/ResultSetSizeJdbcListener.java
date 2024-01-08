package com.example.loggingresultsetdemo;

import com.example.loggingresultsetdemo.vendor.ConsoleTable;
import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.event.JdbcEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ResultSetSizeJdbcListener extends JdbcEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger("p6spy");
    private final Map<Statement, ConsoleTable> table = new ConcurrentHashMap<>();

    @Override
    public void onAfterResultSetNext(ResultSetInformation resultSetInformation, long timeElapsedNanos, boolean hasNext,
            SQLException e) {
        try {
            ResultSet resultSet = resultSetInformation.getResultSet();
            ConsoleTable table = this.table.computeIfAbsent(resultSet.getStatement(), statement -> new ConsoleTable());

            // create table headers
            if (table.size() == 0) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                String[] cells = new String[metaData.getColumnCount()];
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    cells[i] = Optional.ofNullable(metaData.getColumnName(i + 1)).map(Object::toString).orElse("null");
                }
                table.setHeaders(cells);
            }

            // create table row
            List<String> cells = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                try {
                    Object object = resultSet.getObject(i);
                    cells.add(Optional.ofNullable(object).map(Object::toString).orElse("null"));
                } catch (Exception exx) {
                    // last row always fails with "ResultSet not positioned properly, perhaps you need to call next.", not sure why
                    LOGGER.debug("Failed to add cell for column \"{}\"", metaData.getColumnName(i), exx);
                }
            }
            if (!cells.isEmpty()) {
                table.addRow(cells);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onAfterResultSetClose(ResultSetInformation resultSetInformation, SQLException e) {
        try {
            ConsoleTable consoleTable = table.get(resultSetInformation.getResultSet().getStatement());
            if (table.size() > 0) {
                LOGGER.info("\n{}", consoleTable);
                table.remove(resultSetInformation.getResultSet().getStatement());
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
