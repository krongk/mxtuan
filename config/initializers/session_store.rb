# Be sure to restart your server when you modify this file.

# Your secret key for verifying cookie session data integrity.
# If you change this key, all old sessions will become invalid!
# Make sure the secret is at least 30 characters and all random, 
# no regular words or you'll be exposed to dictionary attacks.
ActionController::Base.session = {
  :key         => '_TuanAo_session',
  :secret      => '59167f10d17cb08e4340a74285ec56bcc5bbd347b1390252d2702a122ca83282d38252cce828a04a5468f9cf2b20c9414b8417d3835da6103b7fb1b1eb052554'
}

# Use the database for sessions instead of the cookie-based default,
# which shouldn't be used to store highly confidential information
# (create the session table with "rake db:sessions:create")
# ActionController::Base.session_store = :active_record_store
