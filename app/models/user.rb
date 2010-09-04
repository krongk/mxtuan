require 'digest/sha1'
class User < ActiveRecord::Base
  has_many :orders
  # Virtual attribute for the unencrypted password
  attr_accessor :password

  validates_presence_of     :login, :email,              :message=>"登录名和邮箱不能为空"
  validates_presence_of     :password,                   :if => :password_required?,   :message=>"密码不能为空"
  validates_presence_of     :password_confirmation,      :if => :password_required?,   :message=>"确认密码不能为空"
  validates_length_of       :password, :within => 4..40, :if => :password_required?,   :message=>"密码长度不够,密码至少为4位"
  validates_confirmation_of :password,                   :if => :password_required?,      :message=>"两次输入的密码不匹配"
  validates_length_of       :login,    :within => 3..40, :message=>"登录名的长度至少为3"
  validates_length_of       :email,    :within => 3..100, :message=>"电子邮件长度或格式不正确"
  validates_uniqueness_of   :login, :email, :case_sensitive => false, :message=>"对不起，你所使用的登录名已被占用，请换一个试试"
  validates_uniqueness_of   :email, :case_sensitive => false, :message=>"对不起，你所使用的电子邮箱已被占用，请换一个试试"
  before_save :encrypt_password
  before_create :make_activation_code 
  # prevents a user from submitting a crafted form that bypasses activation
  # anything else you want your user to change should be added here.
  attr_accessible :login, :email, :password, :password_confirmation

  # Activates the user in the database.
  def activate
    @activated = true
    self.activated_at = Time.now.utc
    self.activation_code = nil
    save(false)
  end

  def active?
    # the existence of an activation code means they have not activated yet
    activation_code.nil?
  end

  # Authenticates a user by their login name and unencrypted password.  Returns the user or nil.
  def self.authenticate(login, password)
    #u = find :first, :conditions => ['login = ? and activated_at IS NOT NULL', login] # need to get the salt
    u = find :first, :conditions => ['login = ?', login] # need to get the salt
    u && u.authenticated?(password) ? u : nil
  end

  # Encrypts some data with the salt.
  def self.encrypt(password, salt)
    Digest::SHA1.hexdigest("--#{salt}--#{password}--")
  end

  # Encrypts the password with the user salt
  def encrypt(password)
    self.class.encrypt(password, salt)
  end

  def authenticated?(password)
    crypted_password == encrypt(password)
  end

  def remember_token?
    remember_token_expires_at && Time.now.utc < remember_token_expires_at 
  end

  # These create and unset the fields required for remembering users between browser closes
  def remember_me
    remember_me_for 2.weeks
  end

  def remember_me_for(time)
    remember_me_until time.from_now.utc
  end

  def remember_me_until(time)
    self.remember_token_expires_at = time
    self.remember_token            = encrypt("#{email}--#{remember_token_expires_at}")
    save(false)
  end

  def forget_me
    self.remember_token_expires_at = nil
    self.remember_token            = nil
    save(false)
  end

  # Returns true if the user has just been activated.
  def recently_activated?
    @activated
  end
  
  protected
    # before filter 
    def encrypt_password
      return if password.blank?
      self.salt = Digest::SHA1.hexdigest("--#{Time.now.to_s}--#{login}--") if new_record?
      self.crypted_password = encrypt(password)
    end
      
    def password_required?
      crypted_password.blank? || !password.blank?
    end
    
    def make_activation_code
      self.activation_code = Digest::SHA1.hexdigest( Time.now.to_s.split(//).sort_by {rand}.join )
    end
    
end
