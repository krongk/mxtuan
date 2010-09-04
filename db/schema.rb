# This file is auto-generated from the current state of the database. Instead of editing this file, 
# please use the migrations feature of Active Record to incrementally modify your database, and
# then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your database schema. If you need
# to create the application database on another system, you should be using db:schema:load, not running
# all the migrations from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20100822121830) do

  create_table "chinabanks", :force => true do |t|
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "emails", :force => true do |t|
    t.integer  "city"
    t.string   "mail"
    t.boolean  "is_ok"
    t.string   "note"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "feedbacks", :force => true do |t|
    t.string   "name"
    t.string   "contact"
    t.text     "message"
    t.boolean  "is_check"
    t.text     "note"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "orders", :force => true do |t|
    t.integer  "user_id"
    t.integer  "product_id"
    t.integer  "quantity"
    t.float    "discount"
    t.float    "order_price"
    t.boolean  "is_pay"
    t.integer  "pay_type"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "phone"
    t.string   "address"
  end

  create_table "products", :force => true do |t|
    t.string   "title"
    t.integer  "total_count"
    t.integer  "base_count"
    t.integer  "sold_count",     :default => 0
    t.float    "original_price"
    t.float    "discount"
    t.float    "now_price"
    t.float    "save_price"
    t.datetime "started_at",     :default => '2010-04-23 16:38:34'
    t.datetime "ended_at",       :default => '2010-04-24 16:38:34'
    t.string   "picture"
    t.text     "summary"
    t.text     "details"
    t.text     "contact"
    t.text     "comment"
    t.integer  "status",         :default => 1
    t.boolean  "is_publish",     :default => true
    t.boolean  "is_ok",          :default => true
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "sellers", :force => true do |t|
    t.string   "name"
    t.string   "contact"
    t.text     "info"
    t.boolean  "is_front"
    t.boolean  "is_checked"
    t.text     "note"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "sessions", :force => true do |t|
    t.string   "session_id", :null => false
    t.text     "data"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "sessions", ["session_id"], :name => "index_sessions_on_session_id"
  add_index "sessions", ["updated_at"], :name => "index_sessions_on_updated_at"

  create_table "users", :force => true do |t|
    t.string   "login"
    t.string   "email"
    t.string   "crypted_password",          :limit => 40
    t.string   "salt",                      :limit => 40
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "remember_token"
    t.datetime "remember_token_expires_at"
    t.string   "activation_code",           :limit => 40
    t.datetime "activated_at"
  end

end
