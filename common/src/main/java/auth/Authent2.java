package auth;

import java.sql.*;
import java.util.Set;

public class Authent2 {
    private Set<CredentialsEntry> users;

    public Authent2() {
//        users = Set.of(
//                new CredentialsEntry("l1", "p1","nickname1"),
//                new CredentialsEntry("l2", "p2","nickname2"),
//                new CredentialsEntry("l3", "p3","nickname3")
//        );
    }

    private static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection("jdbc:Sqlite:users.sqlite");
        return connection;
    }

    public String findNicknameLoginAndPassword(String login, String password) {

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet =statement.executeQuery("select * from user")){
            while(resultSet.next()) {
                if (resultSet.getString("Login").equals(login) && resultSet.getString("Password").equals(password)) {
                    return resultSet.getString("Nickname");
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;

    }

    public void add(String login, String password, String nickname) {
        //entries.add(new Authent.CredentialsEntry(login,password,nickname));
        //сделать запрос на добавление новго юзера в табл
    }

    public static class CredentialsEntry{
        private String login;
        private String password;
        private String nickname;

        public CredentialsEntry(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getNickname() {
            return nickname;
        }
    }
}
//
