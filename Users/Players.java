package Users;

public class Players {

    private int id;
    private String name;
    private int score;
    private String gender;

    // geters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getGender() {
        return gender;
    }

    // seters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // Constructor
    public Players(int id, String name, int score, String gender) {
        this.name = name;
        this.score = score;
        this.gender = gender;
        this.setId(id);
    }

    public void increaseScore(int points) {
        this.setScore(this.getScore() + points);
    }

    public void decreaseScore(int points) {
        this.setScore(this.getScore() - points);
        if (this.score < 0) {
            this.score = 0;
        }
    }

    @Override
    public String toString() {
        return "Players{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", gender='" + gender + '\'' +
                '}';
    }

}