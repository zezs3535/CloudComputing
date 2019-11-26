import java.awt.Menu;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

public class awsTest {

	/*
	 * Cloud Computing, Data Computing Laboratory Department of Computer Science
	 * Chungbuk National University
	 */
	static AmazonEC2 ec2;

	private static void init() throws Exception {
		/*
		 * The ProfileCredentialsProvider will return your [default] credential profile
		 * by reading from the credentials file located at (~/.aws/credentials).
		 */
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (~/.aws/credentials), and is in valid format.", e);

		}
		ec2 = AmazonEC2ClientBuilder.standard().withCredentials(credentialsProvider).withRegion("us-east-1") 
				/* check the region at AWS console*/
				.build();
	}

public static void main(String[] args) throws Exception {
	init();
	Scanner menu = new Scanner(System.in);
	
	while(true)
	{
		System.out.println(" ");
		System.out.println(" ");
		System.out.println("------------------------------------------------------------");
		System.out.println(" Amazon AWS Control Panel using SDK ");
		System.out.println(" ");
		System.out.println(" Cloud Computing, Computer Science Department ");
		System.out.println(" at Chungbuk National University ");
		System.out.println("------------------------------------------------------------");
		System.out.println(" 1. list instance 2. available zones ");
		System.out.println(" 3. start instance 4. available regions ");
		System.out.println(" 5. stop instance 6. create instance ");
		System.out.println(" 7. reboot instance 8. list images ");
		System.out.println(" 99. quit ");
		System.out.println("------------------------------------------------------------");
		System.out.print("Enter an integer: ");
		int number = menu.nextInt();
		switch(number) {
		case 1:
			listInstances();
			break;
		case 6:
			CreateInstances();
			break;
		case 99:
			System.exit(0);
		}
	}
}

public static void listInstances() {
		System.out.println("Listing instances....");
		boolean done = false;
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while (!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);
			for (Reservation reservation : response.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					System.out.printf(
							"[id] %s, " + "[AMI] %s, " + "[type] %s, " + "[state] %10s, " + "[monitoring state] %s",
							instance.getInstanceId(), instance.getImageId(), instance.getInstanceType(),
							instance.getState().getName(), instance.getMonitoring().getState());
				}
				System.out.println();
			}
			request.setNextToken(response.getNextToken());
			if (response.getNextToken() == null) {
				System.out.println("��!");
				done = true;
			}
		}
}

public static void CreateInstances() {
	System.out.println("Create instances....");
	Scanner menu=new Scanner(System.in);
	System.out.println("����� �̹����� �Է��ϼ��� : ");
	String imageId=menu.nextLine();
	System.out.println("����� Key�� �Է��ϼ��� : ");
	String keyName=menu.nextLine();
	//String instanceType=menu.nextLine();
	
	RunInstancesRequest run_request = new RunInstancesRequest()
		    .withImageId(imageId)
		    .withInstanceType(InstanceType.T2Micro)
		    .withKeyName(keyName)
		    .withMaxCount(1)
		    .withMinCount(1);
		RunInstancesResult run_response = ec2.runInstances(run_request);
		
		String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();
}
}

