package be.kdg.runtracker.backend.config;

import java.util.Locale;

/**
 * Created by jenss on 1/03/2017.
 */
public class MQTTConfig {
    private String host = "schadronds.synology.me";
//    private String host = "192.168.0.101";
    private int port = 9883;
    private boolean ssl = true;

    private String user = "mosquitto";
    private String pass = "Team102017";

    private String userTopic;

    private String competitionTopic;

    private int keepalive = 10;

    private Will will;

    public MQTTConfig(long userId) {
        this.userTopic = String.format(Locale.ROOT, "uid-%d", userId);
        this.competitionTopic = null;
        this.will = null;
    }

    public MQTTConfig(long userId, long competitionId) {
        this.userTopic = String.format(Locale.ROOT, "uid-%d", userId);
        this.competitionTopic = String.format(Locale.ROOT, "cid-%d", competitionId);
        this.will = new Will(this.userTopic);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isSsl() {
        return ssl;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getUserTopic() {
        return userTopic;
    }

    public String getCompetitionTopic() {
        return competitionTopic;
    }

    public int getKeepalive() {
        return keepalive;
    }

    public Will getWill() {
        return will;
    }

    private class Will {
        private String topic;
        private String payload = "offline";
        private int qos = 2;
        private boolean retain = false;

        Will(String userTopic) {
            this.topic = userTopic;
        }

        public String getTopic() {
            return topic;
        }

        public String getPayload() {
            return payload;
        }

        public int getQos() {
            return qos;
        }

        public boolean isRetain() {
            return retain;
        }
    }
}
