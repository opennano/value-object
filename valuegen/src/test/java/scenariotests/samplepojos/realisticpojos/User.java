package scenariotests.samplepojos.realisticpojos;

import java.util.Date;
import java.util.List;

public class User extends BaseObject {

  private String name;
  private Date createdDate;
  private List<Role> roles;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }
}
