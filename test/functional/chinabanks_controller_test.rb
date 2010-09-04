require 'test_helper'

class ChinabanksControllerTest < ActionController::TestCase
  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:chinabanks)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create chinabank" do
    assert_difference('Chinabank.count') do
      post :create, :chinabank => { }
    end

    assert_redirected_to chinabank_path(assigns(:chinabank))
  end

  test "should show chinabank" do
    get :show, :id => chinabanks(:one).to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => chinabanks(:one).to_param
    assert_response :success
  end

  test "should update chinabank" do
    put :update, :id => chinabanks(:one).to_param, :chinabank => { }
    assert_redirected_to chinabank_path(assigns(:chinabank))
  end

  test "should destroy chinabank" do
    assert_difference('Chinabank.count', -1) do
      delete :destroy, :id => chinabanks(:one).to_param
    end

    assert_redirected_to chinabanks_path
  end
end
