<?php
	$response = array();
	require_once __DIR__ . '/config.php';

	$data = json_decode(file_get_contents('php://input'), true);
	if (array_key_exists("uid", $data)) {
		$uid = $data["uid"];
		$gender = $data["gender"];
		$skinTone = $data["skin-tone"];
		$hairStyle = $data["hair-style"];
		$hairColour = $data["hair-colour"];
		if ($hairStyle == null) {
			$hairColour = null;
		}
		
		if ($uid != null && $gender != null && $skinTone != null) {
			$con=mysqli_connect(DB_HOST, DB_USERNAME, DB_PASSWORD, DB_DATABASE);

			if (mysqli_connect_errno())
			{
				$response["success"] = 0;
				$response["message"] = "Could not connect to database.";
				echo json_encode($response);
			}

			$result = mysqli_query($con, "UPDATE `pokemon-profile` SET `gender` = '$gender', `started-overworld` = 1, `skin-tone` = '$skinTone', `hair-style` = '$hairStyle', `hair-colour` = '$hairColour' WHERE  `uid` = '$uid'");
			$response["success"] = 1;
			$response["message"] = "Successfully created character for uid $uid.";
			echo json_encode($response);
		} else {
			$response["success"] = 0;
			$response["message"] = "Required field(s): uid, gender and skinTone cannot be null";
		}
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "Required field(s) are missing.";
		echo json_encode($response);
	}
?>