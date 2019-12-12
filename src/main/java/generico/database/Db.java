package generico.database;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Gerenciador do banco de dados.
 ****************************************/
public class Db implements AutoCloseable {

    @FunctionalInterface
    interface ResultadoQuery{
        ResultSet executeQuery();
    }

    private ConectionDB conectionDB;
    private StringJoiner sql;
    private HashMap<String, Object> params;
    private ResultSet rs;

    private final int OPERACAO_EXECUTE = 0;
    private final int OPERACAO_EXECUTEQUERY = 1;

    public Db() {
        this.conectionDB = new ConectionDB();
        this.params = new HashMap();
        this.sql = new StringJoiner(" ");
    }

    /**
     * Inicia transação
     * @return
     * @throws SQLException
     ********************************************/
    public Db initTransaction() throws SQLException {
        this.conectionDB.initTransaction();
        return this;
    } //initTransaction

    /**
     * Limpa variavel que armazena o SQL
     * @return
     **************************************/
    public Db cleanSql(){
        this.sql = new StringJoiner(" ");
        return this;
    } //cleanSql

    /**
     * Concatena texto no SQL
     * @param sql
     * @return
     *************************************/
    public Db sql(String sql) {
        this.sql.add(sql);
        return this;
    } //sql

    /**
     * Adiciona novo parâmetro.
     *
     * @param nome  nome do parâmetro, para buscar no SQL;
     * @param valor valor do parâmetro.
     * @return
     *************************************/
    public Db param(String nome, Object valor) {
        this.params.put(nome, valor);
        return this;
    } //param

    /**
     * Adiciona um conjunto de parâmetros.
     * @param valor
     * @return
     *************************************/
    public Db params(HashMap<String, Object> valor) {
        this.params.putAll(valor);
        return this;
    } //params

    /**
     * Executa sql, para operações sem retorno (INSERT, UPDATE, DELETE)
     * @throws SQLException
     *************************************/
    public Integer executeUpdate() throws SQLException {
        try {
            PreparedStatement pstm = getPreparedStatement();
            cleanSql();
            Integer retorno = pstm.executeUpdate();
            params.clear();
            return retorno;
        } finally {
            if (!this.conectionDB.hasTransaction()) {
                this.conectionDB.closeConexion();
            }
        }
    } //execute

    /**
     * Executa sql, para operações sem retorno (INSERT, UPDATE, DELETE)
     * @throws SQLException
     *************************************/
    public void execute() throws SQLException {
        try {
            PreparedStatement pstm = getPreparedStatement();
            cleanSql();
            pstm.execute();
            params.clear();
        } finally {
            if (!this.conectionDB.hasTransaction()) {
                this.conectionDB.closeConexion();
            }
        }
    } //execute

    /**
     * Executa sql, para operações com retorno (SELECT)
     * @return ResultSet
     * @throws SQLException
     *************************************/
    public ResultSet executeQuery() throws SQLException {
        try {
            PreparedStatement pstm = getPreparedStatement();
            cleanSql();
            this.rs = pstm.executeQuery();
            params.clear();
            return this.rs;
        } finally {
            if (!this.conectionDB.hasTransaction()) {
                this.conectionDB.closeConexion();
            }
        }
    } //executeQuery

    /**
     * Executa o sql:
     *  - Atribui o texto de {SQL} em uma variável auxiliar {AUXSQL};
     *  - Percorre {AUXSQL} buscando os parâmetros identificados com ":"
     *      Ex: update tabela set nome = :nome
     *      ► Troca o texto :parametro pelo sinal "?";
     *      ► Busca em {PARAM} o parametro encontrado
     *          - Se encontrado, popula uma listagem auxiliar de parâmetros {LISTPARAM};
     *          - Se não, gera uma exception.
     *  - Prepara o Statement para executar o sql {PSTM};
     *  - Para cada parametro em {LISTPARAM}, identifica seu tipo e adiciona em {PSTM}
     *  - Se {OPERACAO} == {OPERACAO_EXECUTE}
     *      ► limpa {SQL};
     *      ► executa {PSTM};
     *      ► Se não for uma transação, encerra a conexão. TODO: checar se preciso, visto que agora é AutoClosable
     *  - Se {OPERACAO} == {OPERACAO_EXECUTEQUERY}
     *      ► limpa {SQL};
     *      ► executa {PSTM};
     *      ► retorna o ResultSet da consulta
     *
     * @return
     *  - Se {operacao} = OPERACAO_EXECUTEQUERY: retorna o resultSet;
     *  - Se não retorna null.
     * @throws SQLException
     *************************************/
    private PreparedStatement getPreparedStatement() throws SQLException {
        List<Object> listParam = new ArrayList<>();
        String auxSql = this.sql.toString();
        while (auxSql.indexOf(':') > 0) {
            int i = auxSql.indexOf(":");
            Boolean encontrado = false;
            for (Map.Entry<String, Object> param : this.params.entrySet()) {
                String nome = param.getKey();
                Object valor = param.getValue();
                //Verifica se a regiao de auxSql <i+1,i+1+nomeLength> = nome
                // e se, ou a posição i+1+nomeLength existe
                if (auxSql.regionMatches(i + 1, nome, 0, nome.length()) &&
                    (auxSql.length() <= i + 1 + nome.length() ||
                        !(
                            Character.isLetter(auxSql.charAt(i+1+nome.length())) ||
                            Character.isDigit(auxSql.charAt(i+1+nome.length()))
                        )))
                {
                    auxSql = auxSql.replaceFirst(":" + nome, "?");
                    listParam.add(valor);
                    encontrado = true;
                }
            }
            if (this.params.size()>0 && !encontrado) {
                throw new SQLException("Parâmetro não encontrado");
            }
        }
        PreparedStatement pstm = this.conectionDB.getPreparedStatement(auxSql);
        return setParams(pstm, listParam);
    } //getPreparedStatement

    /**
     * Popula os parâmetros do {pstm} de com os valores de {listParam},
     * de acordo com seu respectivo tipo.
     *
     * @param pstm      PreparedStatement a ser executado;
     * @param listParam Lista de parâmetros.
     *
     * @return pstm atualizado.
     * @throws SQLException
     *************************************/
    private PreparedStatement setParams(PreparedStatement pstm, List<Object> listParam) throws SQLException {
        for (int i = 0; i < listParam.size(); i++) {
            if (listParam.get(i) == null) {
                pstm.setObject(i + 1, null);
            } else if (listParam.get(i).getClass().equals(String.class)) {
                pstm.setString(i + 1, (String) listParam.get(i));
            } else if (listParam.get(i).getClass().equals(Integer.class)) {
                pstm.setInt(i + 1, (Integer) listParam.get(i));
            } else if (listParam.get(i).getClass().equals(BigDecimal.class)) {
                pstm.setBigDecimal(i + 1, (BigDecimal) listParam.get(i));
            } else if (listParam.get(i).getClass().equals(Boolean.class)) {
                pstm.setBoolean(i + 1, (Boolean) listParam.get(i));
            } else if (listParam.get(i).getClass().equals(Double.class)) {
                pstm.setDouble(i + 1, (Double) listParam.get(i));
            } else if (listParam.get(i).getClass().equals(Float.class)) {
                pstm.setFloat(i + 1, (Float) listParam.get(i));
            } else if (listParam.get(i).getClass().equals(Long.class)) {
                pstm.setLong(i + 1, (Long) listParam.get(i));
            } else if (listParam.get(i).getClass().equals(Date.class)) {
                pstm.setDate(i + 1, (Date) listParam.get(i));
            } else if (listParam.get(i).getClass().equals(Timestamp.class)) {
                pstm.setTimestamp(i + 1, (Timestamp) listParam.get(i));
            } else if (listParam.get(i).getClass().equals(LocalDateTime.class)) {
                pstm.setTimestamp(i + 1, Timestamp.valueOf((LocalDateTime) listParam.get(i)));
            } else if (listParam.get(i).getClass().equals(LocalDate.class)) {
                pstm.setDate(i + 1, Date.valueOf((LocalDate) listParam.get(i)));
            } else if (listParam.get(i).getClass().equals(LocalTime.class)) {
                pstm.setTime(i + 1, Time.valueOf((LocalTime) listParam.get(i)));
            }
        }
        return pstm;
    } //setParams

    @Override
    public void close() {
        this.conectionDB.closeConexion();
    } //close

    public void commit() throws SQLException {
        if (this.conectionDB!=null) {
            this.conectionDB.commitTransaction();
        }
        this.conectionDB.closeConexion();
    } //commit

    public void rollback() throws SQLException {
        if (this.conectionDB!=null) {
            this.conectionDB.rollbackTransaction();
        }
        this.conectionDB.closeConexion();
    } //rollback
}