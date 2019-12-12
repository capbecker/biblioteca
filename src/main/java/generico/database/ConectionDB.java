package generico.database;

import generico.Initializer;

import java.sql.*;

/**
 * Classe responsável pela conexão com o banco de dados.
 ******************************************************/
public class ConectionDB {
    private static final String LOGTAG = "CONNECTIONDB";
    private Connection currentConection;

    public ConectionDB() {
        this.currentConection = openConexion();
    }

    /**
     * Efetua a conexão com o banco de dados.
     *******************************************/
    private Connection openConexion() {
        Connection connection = null;
        try {
            Class.forName(Initializer.controllerDb).newInstance();
            connection = DriverManager.getConnection(Initializer.linkDb, Initializer.userDb, Initializer.passwordDb);
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            System.out.println("<"+LOGTAG+"> "+e.getMessage());
        }
        return connection;
    } //openConexion

    /**
     * Fecha a conexão
     *******************************************/
    public void closeConexion() {
        try {
            this.currentConection.close();
        } catch (SQLException e) {
            System.out.println("<"+LOGTAG+"> "+e.getMessage());
        }

    } //closeConexion

    public ResultSet runSqlQuery(String query) throws SQLException {
        final Statement statement;
        statement = this.currentConection.createStatement();
        return statement.executeQuery(query);
    } //runSqlQuery

    public Boolean executeQuery(String query) throws SQLException {
        final Statement statement = this.currentConection.createStatement();
        return statement.execute(query);
    } //executeQuery

    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        return this.currentConection.prepareStatement(query);
    } //getPreparedStatement

    public PreparedStatement getPreparedStatement(String query, int flags) throws SQLException {
        return this.currentConection.prepareStatement(query, flags);
    } //getPreparedStatement

    /**
     * Initilize a transaction in generico.database
     * @throws SQLException If initialization fails
     */
    public void initTransaction() throws SQLException {
        this.currentConection.setAutoCommit(false);
    } //initTransaction

    /**
     * Finish a transaction in generico.database and commit changes
     * @throws SQLException If a rollback fails
     */
    public void commitTransaction() throws SQLException {
        try {
            this.currentConection.commit();
        } catch (SQLException e) {
            if (this.currentConection != null) {
                this.currentConection.rollback();
            }
        } finally {
            this.currentConection.setAutoCommit(false);
        }
    } //commitTransaction

    public boolean hasTransaction() throws SQLException {
        return !this.currentConection.getAutoCommit();
    } //hasTransaction

    /**
     * Finish a transaction in generico.database and commit changes
     * @throws SQLException If a rollback fails
     */
    public void rollbackTransaction() throws SQLException {
       try{
            this.currentConection.rollback();
        } finally {
            this.currentConection.setAutoCommit(false);
        }
    } //rollbackTransaction
}
