function address_next(){
	var rname = $("#rname").val();
	var address = $("#address").val();
	var zip = $("#zip").val();
	var mobile = $("#mobile").val();
	$.post('/buy_ajax/reg_address',{rname:rname,address:address,zip:zip,mobile:mobile},function(data){
		if(data){
			location.href = location.href;
		}
		else{
			alert('error');
			return false;
		}
	})
}

function reg_next(){
	if($("#reg_form").valid()){
		var email = $("#email").val();
		var password = $("#password").val();
		var hidden_oid = $("#hidden_oid").val();
		$.post('/buy_ajax/reg_newuser',{email:email,password:password,hidden_oid:hidden_oid},function(data){
			if(data){
				if(data=='success'){
					$.post('/buy_ajax/reg_address',{hidden_oid:hidden_oid},function(data){
						if(data=='success'){
							//pay_orders_register();
							location.href = "/orders/detail/"+hidden_oid;
						}
						else{
							alert('服务器忙，请稍后再试！');
							return false;
						}
					})
				}else if(data=='failed'){
					alert('此邮箱已被注册！');
					return false;
				}else{
					alert(data);
					return false;
				}
			}
			else{
				alert('该账户已被注册，请换个email地址试试');
			}
		})
	}
	
}

function login_next(){
	if($("#logins_form").valid()){
		var email = $("#login_email").val();
		var password = $("#login_pass").val();
		var hidden_oid = $("#hidden_oid").val();
		$.post('/buy_ajax/login',{email:email,password:password,hidden_oid:hidden_oid},function(data){
			if(data){
				if(data=='success'){
					location.href = "/orders/detail/"+hidden_oid;
				}else{
					alert(data);
				}
				
			}else{
				alert('登录失败，请确认邮箱或密码');
			}
		});
	}
}

function pay_orders_register(){
	var oid = $("#hidden_oid").val();
	var method = $("#hidden_method").val();
	$.ajax({
			async: false,
			type: 'POST',
			url: '/buy_ajax/pay_orders',
			dataType: 'json',
			data: 'oid='+oid+'&pay_method='+method,
			success:function(data){
				if(data.status == 'success'){
					$.openPopupLayer({name: "notify_pay_complete_pop",width: 450,url: "/buy_ajax/show_notify_pay_complete",parameters:{oid:oid},async:false});
					window.open(data.url)
					return false;
				}
				else if(data.status == 'direct_success'){
					location.href = data.url;
					return false;
				}
				else if(data.status == 'login'){
					alert(data.message);
					location.href = location.href;
					return false;
				}
				else{
					alert(data.message);
					return false;
				}
			}
	});
}

function login(){
	var email = $("#email_login").val();
	var password = $("#password_login").val();
	$.post('/buy_ajax/login',{email:email,password:password},function(data){
		if(data){
			location.href = location.href;
		}
		else{
			alert("登陆失败，请检查输入的账号和密码是否正确！");
		}
	})
}

function update_amount(){
	if($("#amount").valid()){
		var amount = parseInt($("#amount").val());
		var coin = $("#coin").val();
		var oid = $("#r_oid").val();
		if(!coin) {
			$("#coin").val(0);
			coin = 0;
		}
		if(!oid) {
			oid = 0;
		}
		var pid = $("#pid").val();
		$.ajax({
			type: 'POST',
			url: '/buy_ajax/update_amount',
			dataType: 'json',
			data: 'pid='+pid+'&amount='+amount+'&coin='+coin+'&oid='+oid,
			success:function(data){
				if(data.status == 'success'){
					$("#total_price").html('￥'+data.total_price);
					$("#pay_total").html('￥'+data.pay_total);
					$("#form_error").val(0);
					if($("#coin").length > 0){
						$("#coin").val(data.token_max);
						$("#pay_total").html('￥'+(Math.round(data.pay_total*100)-(data.token_max*100-coin*100))/100);
						$("#coin_limit").html(data.token_max);
						$("#coin").rules("remove", "max");
						$("#coin").rules("add", {
							digits: true,
							min: 0,
							max: data.token_max,
							messages:{
								digits:"请填入合法的粉币数量",
								min:"请填入合法的粉币数量",
								max:function(){$('#openlayer2').show();return false}
							}
						})
					}
					if(coin > 0){
						if($("#coin").valid()){
							return true;
						}
						else{
							$("#coin").val($("#coin_limit").text());
							update_amount();
							return false;
						}
					}
					else{
						return true;
					}
				}
				else if(data.status == 'failed'){
					$("#form_error").val(1);
					$("#amount_pop_message").html(data.message);
					$("#openlayer1").show();
					return false;
				}
				else if(data.status == 'coin_failed'){
					$("#coin_limit").html(data.token_max);
					$("#coin").rules("remove", "max");
					$("#coin").rules("add", {
						digits: true,
						min: 0,
						max: data.token_max,
						messages:{
							digits:"请填入合法的粉币数量",
							min:"请填入合法的粉币数量",
							max:function(){$('#openlayer2').show();return false}
						}
					});
					$("#coin").valid();
					$("#coin").val($("#coin_limit").text());
					update_coin();
				}
			}
		});
	}
}

function update_coin(){
	if($("#amount").valid()){
		var amount = parseInt($("#amount").val());
		var coin = $("#coin").val();
		var oid = $("#r_oid").val();
		if(!coin) {
			$("#coin").val(0);
			coin = 0;
		}
		if(!oid) {
			oid = 0;
		}
		var pid = $("#pid").val();
		if($("#coin").valid()){
			$.ajax({
				type: 'POST',
				url: '/buy_ajax/update_amount',
				dataType: 'json',
				data: 'pid='+pid+'&amount='+amount+'&coin='+coin+'&oid='+oid,
				success:function(data){
					if(data.status == 'success'){
						$("#total_price").html('￥'+data.total_price);
						$("#pay_total").html('￥'+data.pay_total);
						$("#form_error").val(0);
					}
					else if(data.status == 'failed'){
						$("#form_error").val(1);
						$("#amount_pop_message").html(data.message);
						$("#openlayer1").show();
					}
					else if(data.status == 'coin_failed'){
						$("#coin_limit").html(data.token_max);
						$("#coin").rules("remove", "max");
						$("#coin").rules("add", {
							digits: true,
							min: 0,
							max: data.token_max,
							messages:{
								digits:"请填入合法的粉币数量",
								min:"请填入合法的粉币数量",
								max:function(){$('#openlayer2').show();return false}
							}
						});
						$("#coin").valid();
						$("#coin").val($("#coin_limit").text());
						update_coin();
					}
				}
			});
		}
		else{
			$("#coin").val($("#coin_limit").text());
		}
	}
}


function show_coinarea(){
	if($("#use_coin").val()==1){
		$("#use_coin").val(2);
		$("#coinarea").show();
	}
	else{
		$("#use_coin").val(1);
		$("#coinarea").hide();
		$("#coin").val('');
	}
}

function virtual_show_coinarea(){
	if($("#virtual_coin").val()==1){
		$("#virtual_coin").val(2);
		$("#virtual_coinarea").show();
	}
	else{
		$("#virtual_coin").val(1);
		$("#virtual_coinarea").hide();
		$("#coins").val('');
	}
}

function virtual_reg_next(){
	var reg_rname = $("#reg_rname").val();
	var reg_email = $("#reg_email").val();
	var reg_password = $("#reg_password").val();
	$.post('/buy_ajax/virtual_reg_newuser',{vir_rname:reg_rname,vir_email:reg_email,vir_password:reg_password},function(data){
		if(data){
			var reg_sex = $("input[@name=reg_sex][@checked]").val();
			var reg_mobile = $("#reg_mobile").val();
			$.post('/buy_ajax/virtual_reg_address',{vir_rname:reg_rname,vir_email:reg_email,vir_sex:reg_sex,vir_mobile:reg_mobile},function(data){
				if(data){
					location.href = location.href;
				}
				else{
					alert('error');
					return false;
				}
			})
		}
		else{
			alert('该账户已被注册，请换个email地址试试')
		}
	})
}

function virtual_addinfo_next(){
	var add_name = $("#add_name").val();
	var add_mobile = $("#add_mobile").val();
	
	$.post('/buy_ajax/virtual_addinfo',{add_name:add_name,add_mobile:add_mobile},function(data){
		if(data){
			location.href = location.href;
		}
		else{
			alert('error');
			return false;
		}
	});
}

function v_address(){
	var r_address = $("input[name=r_address]:checked").val();
	if(r_address == 0){
		alert('这是默认地址！！！！');
	}else if(r_address == 1){
		alert('这是备用地址！！！！');
	}
}

function pay_orders(){
	if($("#tab_cont8").css("display")=='none' || $("#tab_cont1").css("display")=='none' || $("#tab_cont6").css("display")=='none'){
		alert('请您确认修改后再提交订单');
		return false;
	}
	var oid = $("#r_oid").val();
	if(($("#pay_method_form").length >0 && $("#pay_method_form").valid()) || ($("#pay_method_form").length == 0)){
		$("#submit_but").attr("disabled","true");
		var pay_method = $("input[name='pay_method']:checked").val();
		$.ajax({
				async: false,
				type: 'POST',
				url: '/buy_ajax/pay_orders',
				dataType: 'json',
				data: 'oid='+oid+'&pay_method='+pay_method,
				success:function(data){
					if(data.status == 'success'){
						$.openPopupLayer({name: "notify_pay_complete_pop",width: 450,url: "/buy_ajax/show_notify_pay_complete",parameters:{oid:oid},async:false});
						window.open(data.url)
						return false;
					}
					else if(data.status == 'direct_success'){
						location.href = data.url;
						return false;
					}
					else if(data.status == 'login'){
						alert(data.message);
						location.href = location.href;
						return false;
					}
					else{
						alert(data.message);
						return false;
					}
				}
		});
	}
}

function pay_fail_orders(){
	$("#submit_but").attr("disabled","true");
	var oid = $("#r_oid").val();
	var pay_method = $("input[name='pay_method']:checked").val()
	$.ajax({
			async: false,
			type: 'POST',
			url: '/buy_ajax/pay_orders',
			dataType: 'json',
			data: 'oid='+oid+'&pay_method='+pay_method,
			success:function(data){
				if(data.status == 'success'){
					$.openPopupLayer({name: "notify_pay_complete_pop",width: 450,url: "/buy_ajax/show_notify_pay_complete",parameters:{oid:oid},async:false});
					window.open(data.url);
					return false;
				}
				else if(data.status == 'direct_success'){
					location.href = data.url;
					return false;
				}
				else if(data.status == 'login'){
					alert(data.message);
					location.href = location.href;
					return false;
				}
				else{
					alert(data.message);
					return false;
				}
			}
	});
}

function close_total_fee(){
	$('#big_open').hide();
}

function show_address(){
	$("#r_other_address").show();
}
function f_show_address(){
	$("#f_other_address").show();
}

function open_r_box_address(){
	if($("#checbox_box").attr('checked') == true){
		$('#h_big_tab').show();
	}else{
		$('#h_big_tab').hide();
	}
}

function alter_address(){
	$('#tab_cont2').show();
	$('#tab_cont1').hide();
}

function assess_address(){
	if($("#address_form").valid()){
		var r_name = $("#r_name").val();
		var r_mobile = $("#r_mobile").val();
		var r_address = $("#r_address").val();
		var r_oid = $("#r_oid").val();
		var hidden_target = $("#hidden_target").val();
		
		$.post('/buy_ajax/assess_address',{r_name:r_name,r_mobile:r_mobile,r_address:r_address,r_oid:r_oid,hidden_target:hidden_target},function(data){
			if(data == 'success'){
				location.href = location.href;
				$('#tab_cont1').show();
				$('#tab_cont2').hide();
			}
			else{
				alert('系统忙！请稍后再试！！！');
			}
		});
	}
}

function alter_r_date(){
	$('#tab_cont5').show();
	$('#tab_cont6').hide();
}

function assess_r_date(){
	var r_date = $("input[name=r_date]:checked").val();
	var r_oid = $("#r_oid").val();
	$.post('/buy_ajax/assess_r_date',{r_date:r_date,r_oid:r_oid},function(data){
		if(data){
			location.href = location.href;
		}
		else{
		}
	});
}

function alter_amount(){
	$('#tab_cont7').show();
	$('#tab_cont8').hide();
}

function assess_amount(){
	if($("#amount").valid()){
		var amount = parseInt($("#amount").val());
		var coin = $("#coin").val();
		var oid = $("#r_oid").val();
		if(!coin) {
			$("#coin").val(0);
			coin = 0;
		}
		if(!oid) {
			oid = 0;
		}
		$.ajax({
			type: 'POST',
			url: '/buy_ajax/update_amount',
			dataType: 'json',
			data: 'amount='+amount+'&coin='+coin+'&oid='+oid,
			success:function(data){
				if(data.status == 'success'){
					$("#form_error").val(0);
					if($("#coin").length > 0){
						$("#coin").val(data.token_max);
						$("#pay_total").html('￥'+(Math.round(data.pay_total*100)-(data.token_max*100-coin*100))/100);
						$("#coin_limit").html(data.token_max);
						$("#coin").rules("remove", "max");
						$("#coin").rules("add", {
							digits: true,
							min: 0,
							max: data.token_max,
							messages:{
								digits:"请填入合法的粉币数量",
								min:"请填入合法的粉币数量",
								max:function(){$('#openlayer2').show();return false}
							}
						})
					}
					if(coin > 0){
						if($("#coin").valid()){
							var amount_valid = true;
						}
						else{
							var amount_valid = false;
						}
					}
					else{
						var amount_valid = true;
					}
					if(amount_valid){
						$.post('/buy_ajax/assess_amount',{amount:amount,coin:coin,oid:oid},function(data){
							if(data){
								location.href = location.href;
							}
						})
					}
				}
				else{
					$("#form_error").val(1);
					alert(data.message);
					return false;
				}
			}
		});
	}
}

function check_notify(obj){
	$("input[name='notify']").attr('checked',$(obj).attr('checked'));
}

function notify_friend_save(){
	if($("#notify_friend_form input[name='notify_type']:checked").val() == 1){
		$("#notify_content2").rules("remove","maxlength");
		$("#notify_friend_form input[name='notify_mobile']").rules("remove","digits minlength");
		$("#notify_friend_form input[name='notify_email']").rules("add",{
			email: true,
			messages:{email:"请填入合法的email地址"}
		});
	}
	else{
		$("#notify_friend_form input[name='notify_email']").rules("remove", "email");
		$("#notify_content2").rules("add", {
			maxlength: 70,
			messages: "<br />字数超过上限，最多只能70个字"
		});
		$("#notify_friend_form input[name='notify_mobile']").rules("add",{
			digits: true,
			minlength: 11,
			messages:{
				digits:"请填入合法的手机号",
				minlength:"请填入合法的手机号"
			}
		});
	}
	if($("#notify_friend_form").valid()){
		var oid = $("#oid").val();
		var notify_email = '';
		var notify_subject = '';
		var notify_mobile = '';
		var notify_from = $.trim($("#notify_friend_form input[name='notify_from']").val());
		var notify_to = $.trim($("#notify_friend_form input[name='notify_to']").val());
		if($("#notify_friend_form input[name='notify_type']:checked").val() == 1){
			var notify_type = 1;
			notify_email = $.trim($("#notify_friend_form input[name='notify_email']").val());
			notify_subject = $.trim($("#notify_friend_form input[name='notify_subject']").val());
			var notify_content = $.trim($("#notify_friend_form textarea[name='notify_content1']").val());
		}
		else{
			var notify_type = 2;
			notify_mobile = $.trim($("#notify_friend_form input[name='notify_mobile']").val());
			var notify_content = $.trim($("#notify_friend_form textarea[name='notify_content2']").val());
		}
		$.post('/buy_ajax/save_notify_form',{oid:oid,notify_from:notify_from,notify_to:notify_to,notify_type:notify_type,notify_email:notify_email,notify_subject:notify_subject,notify_mobile:notify_mobile,notify_content:notify_content},function(data){
			if(data=='success'){
				$.closePopupLayer('notify_friend_pop');	
			}
			else{
				alert(data);
			}
		})
	}
}

function change_notify_type(v){
	if(v==1){
		$("#emailblock1,#emailblock2").show();
		$("#mobileblock").hide();
	}
	else{
		$("#mobileblock").show();
		$("#emailblock1,#emailblock2").hide();
	}
}

function confirm_pay_complete(){
	if($("#r_oid").val()){
		var oid = $("#r_oid").val();
	}
	else if($("#hidden_oid").val()){
		var oid = $("#hidden_oid").val();
	}
	if(oid){
		$.post('/buy_ajax/confirm_pay_complete',{oid:oid},function(data){location.href = data});
	}
}

function up_r_address(id){
	var rp_name = $("#r_name_"+id).text();
	var rp_mobile = $("#r_mobile_"+id).text();
	var rp_address = $("#r_address_"+id).text();
	var r_name = $("#r_name").val(rp_name);
	var r_mobile = $("#r_mobile").val(rp_mobile);
	var r_address = $("#r_address").val(rp_address);
	$("#r_other_address").hide();
}
function up_f_address(id){
	var fp_name = $("#f_name_"+id).text();
	var fp_mobile = $("#f_mobile_"+id).text();
	var fp_address = $("#f_address_"+id).text();
	var r_name = $("#f_name").val(fp_name);
	var r_mobile = $("#f_mobile").val(fp_mobile);
	var r_address = $("#f_address").val(fp_address);
	$("#f_other_address").hide();
}
function friend_address(){ 
	$("#dt_name").text("您朋友的姓名:");
	$("#dt_mobile").text("您朋友的手机:");
	$("#dt_address").text("您朋友的地址:");
	$("#hidden_target").val(2);
	$("#friends_address").hide();
	$("#self_address").show();
	$("#liuc_tab01").text("身份证是您朋友到店消费的凭证");
	$("#liuc_tab02").text("请正确填写您朋友的手机号，及时获得发货通知或消费通知！");
	
}

function self_address(){
	$("#dt_name").text("您的姓名:");
	$("#dt_mobile").text("您的手机:");
	$("#dt_address").text("您的地址:");
	$("#hidden_target").val(1);
	$("#friends_address").show();
	$("#self_address").hide();
	$("#liuc_tab01").removeClass("liuc_tab02_x1 text5");
	$("#liuc_tab01").addClass("liuc_tab02_x2 text5");
	$("#liuc_tab01").text("身份证是您到店消费的凭证");
	$("#liuc_tab02").text("请正确填写您的手机号，及时获得发货通知或消费通知！");
}

function copyUrl(obj){
	var url = $("#"+obj).val();
	if(window.clipboardData){
		window.clipboardData.setData("Text",url);
	}
	$("#msnwin").hide();
}

function enterreg(){
	if(event.keyCode == 13){
		reg_next();
	}
}

function enterlogin(){
	if(event.keyCode == 13){
		login_next();
	}
}

/*firefox*/
function __firefox(){
    HTMLElement.prototype.__defineGetter__("runtimeStyle", __element_style);
    window.constructor.prototype.__defineGetter__("event", __window_event);
    Event.prototype.__defineGetter__("srcElement", __event_srcElement);
}
function __element_style(){
    return this.style;
}
function __window_event(){
    return __window_event_constructor();
}
function __event_srcElement(){
    return this.target;
}
function __window_event_constructor(){
    if(document.all){
        return window.event;
    }
    var _caller = __window_event_constructor.caller;
    while(_caller!=null){
        var _argument = _caller.arguments[0];
        if(_argument){
            var _temp = _argument.constructor;
            if(_temp.toString().indexOf("Event")!=-1){
                return _argument;
            }
        }
        _caller = _caller.caller;
    }
    return null;
}
if(window.addEventListener){
    __firefox();
}
/*end firefox*/

function save_address(){
	var r_name = $("#r_name").val();
	var r_mobile = $("#r_mobile").val();
	var r_address = $("#r_address").val();
	var hidden_uid = $("#hidden_uid").val();
	$.post('/buy_ajax/save_address',{r_name:r_name,r_mobile:r_mobile,r_address:r_address,hidden_uid:hidden_uid},function(data){
		if(data == 'success'){
			alert('添加成功！');
		}
		else if(data == 'failed'){
			alert('添加失败！');
			return false;
		}
		else if(data == 'full'){
			alert('地址数量已经超过3个,请到个人中心进行修改。');
			return false;
		}
	})
}

function show_pay_method_tips(v){
	if(v==1){
		$('#pay_method_tips').show();
	}
	else{
		$('#pay_method_tips').hide();
	}
}