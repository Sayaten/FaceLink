package Server;

public class ProfileModifyReq {
	private String screen_name;
	private String name;
	private String gender;
	private String country;
	private String job;
	private String profile_img;
	
	ProfileModifyReq() {
		screen_name = null;
		name = null;
		gender = null;
		country = null;
		job = null;
		profile_img = null;
	}
	
	public String getScreen_name() {
		return screen_name;
	}
	public void setScreen_name(String screen_name) {
		this.screen_name = new String(screen_name);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = new String(name);
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = new String(gender);
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = new String(country);
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = new String(job);
	}
	public String getProfile_img() {
		return profile_img;
	}
	public void setProfile_img(String profile_img) {
		this.profile_img = new String(profile_img);
	}
}
