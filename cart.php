<html>
<head>
<title>Shopping cart -- Grocery Store</title>
</head>
<body>
<h2>Your shopping cart</h2>
<?php
session_start();
if (isset($_REQUEST["orderItemID"])) 
{
	if (empty($_SESSION["orderitemid"])) 
		{
		$_SESSION["orderitemid"][0]	=$_REQUEST["orderItemID"];
		$_SESSION["ordername"][0]	=$_REQUEST["orderItemName"];
		$_SESSION["orderprice"][0]	=$_REQUEST["orderPrice"];
		$_SESSION["orderuqty"][0]	=$_REQUEST["orderUQty"];
		$_SESSION["orderqty"][0]	=$_REQUEST["orderQty"];
		}
	else
		{
			if (in_array($_REQUEST['orderItemID'], $_SESSION['orderitemid']))
			{
			$ez = $_REQUEST["orderItemID"];
			$bb = $_SESSION["orderitemid"];
			$key = array_search($ez, $bb);
			echo "<i><b>Product already exists. New quantity was updated</b></i><br/><br/>";
			$_SESSION["orderqty"][$key]	=$_REQUEST["orderQty"];
			}
			else
			{
			$_SESSION["orderitemid"][]	=$_REQUEST["orderItemID"];
			$_SESSION["ordername"][] 	=$_REQUEST["orderItemName"];
			$_SESSION["orderprice"][]	=$_REQUEST["orderPrice"];
			$_SESSION["orderuqty"][]	=$_REQUEST["orderUQty"];
			$_SESSION["orderqty"][]		=$_REQUEST["orderQty"];
			}
		}
}
else
{
	print ("<b>Nothing was added!</b><br>");
}

if (isset($_SESSION["orderitemid"]))
{
	print "<table border=1 cellpadding='4' cellspacing='4' width=100% background-color='#FFFFB5'>";
	print "<tr align=center>\n";
	print "<th border = '1'>Product Name</th>";
	print "<th border = '1'>Unit Price</th>";
	print "<th border = '1'>Unit Quantity</th>";
	print "<th border = '1'>Ordered Quantity</th>";
	print "</tr>";
	for ($i=0; $i < sizeof($_SESSION['orderitemid']); $i++)
		{
		print "<tr align=center>\n";
		print "<td border = '1'>".$_SESSION['ordername'][$i]."</td>";
		print "<td border = '1'>".$_SESSION['orderprice'][$i]."</td>";
		print "<td border = '1'>".$_SESSION['orderuqty'][$i]."</td>";
		print "<td border = '1'>".$_SESSION['orderqty'][$i]."</td>";
		print "</tr>";
		}
	print "</table><br/>";
	print "<div style=\"text-align:right;\"><b>Totoal Amount:</b>";
		for ($i=0; $i < sizeof($_SESSION['orderitemid']);$i++)
		{
		$out[$i] = ($_SESSION['orderprice'][$i]*$_SESSION['orderqty'][$i]);
		}
	$totpr = array_sum($out);
	echo $totpr;
	echo "<br/><br/></div>";
	$_SESSION['totpr']=$totpr;
	print "<div style=\"text-align:right;\"><input type=\"button\" name=\"empty\" value=\"Clear the Cart\" 
onclick=\"location.href='empty.php'\">&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"button\" name=\"checkout\" value=\"Check Out\" target=\"rtop\" 
onclick=\"parent.rtop.location='checkout.php'\"><br/><br/></div>";
}
else
{ 
print "The cart is empty.<br/>";
}
?>
</body>
</html>