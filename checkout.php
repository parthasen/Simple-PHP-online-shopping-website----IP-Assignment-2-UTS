<html>
<head>
<title>Check out -- Grocery Store</title>
<script language="javascript">
function validate()
{
if (enterdetail.name.value=="")
{
    alert("Please enter your name");
    return false;
}
if (enterdetail.address.value=="")
{
    alert("Please enter your address");
    return false;
}
if (enterdetail.suburb.value=="")
{
    alert("Please enter your suburb");
    return false;
}
if (enterdetail.state.value=="")
{
    alert("Please enter your state");
    return false;
}
if (enterdetail.country.value=="")
{
    alert("Please enter your country");
    return false;
}
if (enterdetail.email.value=="")
{
    alert("Please enter your email");
    return false;
}
var valiemail = /^[\w\-\.]+@[a-z0-9]+(\-[a-z0-9]+)?(\.[a-z0-9]+(\-[a-z0-9]+)?)*\.[a-z]{2,4}$/i;   
var emailpass=valiemail.test(document.enterdetail.email.value);  
if (emailpass==false)
{  
 alert("invalid email address!"); 
 return false;
}  
else
{
 return true;
 }
}
</script>
</title>
</head>
<body>
<h2>Check out</h2>
<?php
session_start();
$name = $_REQUEST['name'];
if (!empty($_SESSION['orderqty']))
//if nothing is in the cart , go back
{
if (!empty($_REQUEST['email']) && !empty($_REQUEST['name']) && !empty($_REQUEST['address']) && !empty($_REQUEST['suburb']) && !empty($_REQUEST['state']) && !empty($_REQUEST['country']))
//if all fields are filled out, send email
  {
  $email = $_REQUEST['email'] ;
  $subject = "Your new order in Grocery Store";
  $message = '<b>Grocery Store online system:</b><br/><br/>
  <i>Here is the detail of your order:</i><br/><br/>
  <table border=1 cellpadding=4 cellspacing=4>
  <tr align=center>
  <th>Product Name</th>
  <th>Ordered Quantity</th>
  </tr>';
	for ($i=0; $i < sizeof($_SESSION["orderqty"]); $i++)
		{
		$message.= '<tr align=center><td>'.$_SESSION["ordername"][$i].'</td><td>'.$_SESSION["orderqty"][$i].'</td></tr>';
		}
  $message.="
  </table><br/>
  <i>Here is your detail:</i><br/><br/>
  <b>Name:</b> $_REQUEST[name] <br/>
  <b>Address:</b> $_REQUEST[address] <br/>
  <b>Suburb:</b> $_REQUEST[suburb] <br/>
  <b>State:</b> $_REQUEST[state] <br/>
  <b>Coutry:</b> $_REQUEST[country] <br/>  <br/>
  <b>Total price:</b> $_SESSION[totpr] <br/><br/>
  Thank you for shopping in our store!<br/><br/><br/><br/>
  ";
  mail($email,$subject,$message,'Content-Type:text/html');
  mail('ipbackup@dz4.us',$email,$message,'Content-Type:text/html');
  echo "<b>Thank you for shopping in our store! </b><br/>";
  session_destroy();
  print "Notice: Cart has been emptyed";
  print "<script language='javascript'>parent.rbottom.location.href='cart.php'; </script>";
  }
else
//if one field is not filled out, display the form
  {
  echo "<form name='enterdetail' method='post' action='checkout.php'>
  <table border=0>
  <tr>
  <td>Name:</td>
  <td><input name='name' type='text' /></td>
  </tr>
  <tr>
  <td>Address:</td>
  <td><input name='address' type='text' /></td>
  </tr>
  <tr>
  <td>Suburb:</td>
  <td><input name='suburb' type='text' /></td>
  </tr>
  <tr>
  <td>State:</td>
  <td><input name='state' type='text' /></td>
  </tr>
  <tr>
  <td>Country:</td>
  <td><input name='country' type='text' /></td>
  </tr>
  <tr>
  <td>Email:</td>
  <td><input name='email' type='text' /></td>
  </tr>
  <tr>
  <td><input  type='submit' value='Purchase' onclick='return validate()'/></td>
  </tr>
  </form>";
  }
  }
  else
  { echo "Nothing is in the cart. <br/>Please Select an item from the products on the left hand side.";
    print "<script language='javascript'>parent.rbottom.location.href='cart.php'; </script>";
  }
?>