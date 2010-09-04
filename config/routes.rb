ActionController::Routing::Routes.draw do |map|
  map.resources :users do |m|
    m.resources :orders
  end

  map.resources :chinabanks
  map.resources :emails

  map.resources :orders

  map.resources :feedbacks
  map.resources :sellers
  map.resources :users
  map.resource :session
  map.resources :products
 
  # The priority is based upon order of creation: first created -> highest priority.

  # Sample of regular route:
  #   map.connect 'products/:id', :controller => 'catalog', :action => 'view'
  # Keep in mind you can assign values other than :controller and :action

  # Sample of named route:
  #   map.purchase 'products/:id/purchase', :controller => 'catalog', :action => 'purchase'
  # This route can be invoked with purchase_url(:id => product.id)

  # Sample resource route (maps HTTP verbs to controller actions automatically):
  #   map.resources :products

  # Sample resource route with options:
  #   map.resources :products, :member => { :short => :get, :toggle => :post }, :collection => { :sold => :get }

  # Sample resource route with sub-resources:
  #   map.resources :products, :has_many => [ :comments, :sales ], :has_one => :seller
  
  # Sample resource route with more complex sub-resources
  #   map.resources :products do |products|
  #     products.resources :comments
  #     products.resources :sales, :collection => { :recent => :get }
  #   end

  # Sample resource route within a namespace:
  #   map.namespace :admin do |admin|
  #     # Directs /admin/products/* to Admin::ProductsController (app/controllers/admin/products_controller.rb)
  #     admin.resources :products
  #   end

  # You can have the root of your site routed with map.root -- just remember to delete public/index.html.
  map.root :controller => "welcome",:action=>'today'

  # See how all your routes lay out with "rake routes"
  map.signup '/signup', :controller => 'users', :action => 'new'
	map.login '/login', :controller => 'sessions', :action => 'new'
	map.logout '/logout', :controller => 'sessions', :action => 'destroy'
  map.profile '/profile', :controller => 'welcome', :action => 'profile'
  map.today     '/today',    :controller => "welcome",:action=>'today'
  map.tomorrow  '/tomorrow',  :controller => "welcome",:action=>'tomorrow'
  map.yesterday '/yesterday', :controller => "welcome",:action=>'yesterday'
  map.add_to_cart '/add_to_cart', :controller=>'welcome',:action=>'add_to_cart'
  map.add_to_order '/add_to_order',:controller=>'welcome',:action=>'add_to_order'
  map.forget_password '/account/forget_password',:controller=>'users',:action=>'forget_password'
  map.reset_password '/account/reset_password',:controller=>'users',:action=>'reset_password'

  # Install the default routes as the lowest priority.
  # Note: These default routes make all actions in every controller accessible via GET requests. You should
  # consider removing or commenting them out if you're using named routes and resources.
  map.connect ':controller/:action/:id'
  map.connect ':controller/:action/:id.:format'
 
end
