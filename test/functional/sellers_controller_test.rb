require 'test_helper'

class SellersControllerTest < ActionController::TestCase
  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:sellers)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create seller" do
    assert_difference('Seller.count') do
      post :create, :seller => { }
    end

    assert_redirected_to seller_path(assigns(:seller))
  end

  test "should show seller" do
    get :show, :id => sellers(:one).to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => sellers(:one).to_param
    assert_response :success
  end

  test "should update seller" do
    put :update, :id => sellers(:one).to_param, :seller => { }
    assert_redirected_to seller_path(assigns(:seller))
  end

  test "should destroy seller" do
    assert_difference('Seller.count', -1) do
      delete :destroy, :id => sellers(:one).to_param
    end

    assert_redirected_to sellers_path
  end
end
