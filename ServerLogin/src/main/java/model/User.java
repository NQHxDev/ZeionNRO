package model;

import nro.data.dao.AccountDAO;
import nro.data.model.Account;
import io.Session;
import server.Server;
import lombok.Getter;

@Getter
public class User {

   private Session session;

   private Account account;

   private int serverID;

   private int clientID;

   public User(String username, String password, int serverID, int clientID, Session session) {
      this.serverID = serverID;
      this.clientID = clientID;
      this.session = session;
      this.account = AccountDAO.findAccount(username, password);
   }

   public boolean login() {
      if (this.account == null) {
         this.session.getService().loginFailed(this.clientID,
               "Thông tin tài khoản hoặc mật khẩu không chính xác");
         return false;
      }

      if (this.account.getServerLogin() != this.serverID) {
         this.session.getService().loginFailed(this.clientID,
               "Vui lòng truy cập NROBLUE.ONLINE để thiết lập Server Đăng Nhập.");
         return false;
      }

      User us = UserManager.getInstance().find(this.account.getId());
      if (us != null) {
         us.disconnect();
      }

      if (this.account.isBan()) {
         this.session.getService().loginFailed(this.clientID,
               "Tài khoản đã bị khóa do vi phạm điều khoản!");
         return false;
      }

      if (!this.account.isAdmin() && Server.getInstance().getConfig().getTestmode() == 1) {
         this.session.getService().loginFailed(this.clientID,
               "Server đang bảo trì, vui lòng quay lại sau!");
         return false;
      }

      this.session.getService().loginSuccessful(this);
      return true;
   }

   public void disconnect() {
      if (this.account != null) {
         this.session.getService().disconnect(this.account.getId());
         UserManager.getInstance().remove(this);
      }
   }

   public int getUserID() {
      return this.account != null ? this.account.getId() : -1;
   }

   public String getUsername() {
      return this.account != null ? this.account.getUsername() : "";
   }

   public boolean isAdmin() {
      return this.account != null && this.account.isAdmin();
   }

   public void setUserID(int userID) {
      if (this.account == null) {
         this.account = new Account();
      }
      this.account.setId(userID);
   }

   public boolean isActived() {
      return this.account != null && this.account.isActive();
   }

   public int getGoldBar() {
      return this.account != null ? this.account.getGoldBar() : 0;
   }

   public long getLastTimeLogin() {
      return (this.account != null && this.account.getLastTimeLogin() != null)
            ? this.account.getLastTimeLogin().getTime()
            : 0;
   }

   public long getLastTimeLogout() {
      return (this.account != null && this.account.getLastTimeLogout() != null)
            ? this.account.getLastTimeLogout().getTime()
            : 0;
   }

   public String getRewards() {
      return this.account != null ? this.account.getReward() : "";
   }

   public int getRuby() {
      return this.account != null ? this.account.getRuby() : 0;
   }

   public int getMocNap() {
      return this.account != null ? this.account.getCountCard() : 0;
   }

   public int getServer() {
      return this.account != null ? this.account.getServerLogin() : 0;
   }

}
