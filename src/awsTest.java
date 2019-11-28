import java.util.Collection;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
public class awsTest {

	static AmazonEC2 ec2;

	private static void init() throws Exception {
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (~/.aws/credentials), and is in valid format.", e);

		}
		ec2 = AmazonEC2ClientBuilder.standard().withCredentials(credentialsProvider).withRegion("us-east-1") 
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
		System.out.println(" 2015041032 장찬용 ");
		System.out.println(" Chungbuk National University");
		System.out.println("------------------------------------------------------------");
		System.out.println(" 1. list instance 2. available zones ");
		System.out.println(" 3. start instance 4. available regions ");
		System.out.println(" 5. stop instance 6. create instance ");
		System.out.println(" 7. reboot instance 8. list images ");
		System.out.println(" 9. terminate instance 10. x");
		System.out.println(" 99. quit ");
		System.out.println("------------------------------------------------------------");
		System.out.print("Enter an integer: ");
		int number = menu.nextInt();
		switch(number) {
		case 1:
			System.out.println();
			listInstances();
			break;
		case 2:
			System.out.println();
			AvailableZones();
			break;
		case 3:
			System.out.println();
			StartInstances();
			break;
		case 5:
			System.out.println();
			StoppingInstances();
			break;
		case 6:
			System.out.println();
			CreateInstances();
			break;
		case 8:
			System.out.println();
			ImageList();
			break;
		case 9:
			System.out.println();
			TerminateInstances();
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
				System.out.println("끝!");
				done = true;
			}
		}
}

public static void AvailableZones() {
	DescribeAvailabilityZonesResult zRequest = ec2.describeAvailabilityZones();
	for(AvailabilityZone zone:zRequest.getAvailabilityZones()) {
		System.out.printf("[Zone name : %s]"+"[Zone id : %s]",zone.getZoneName(),zone.getZoneId());
		System.out.println();
	}
}
public static void StartInstances() {
	System.out.println("Start instances");
	Scanner menu = new Scanner(System.in);
	System.out.print("시작할 인스턴스의 id를 입력하세요 : ");
	String instanceId=menu.nextLine();
	StartInstancesRequest sRequest=new StartInstancesRequest().withInstanceIds(instanceId);
	ec2.startInstances(sRequest);
	System.out.printf("%s 인스턴스 시작 완료",instanceId);
}
public static void StoppingInstances() {
	System.out.println("Stop instances");
	Scanner menu=new Scanner(System.in);
	System.out.print("중지할 인스턴스의 id를 입력하세요 : ");
	String instanceId=menu.nextLine();
	StopInstancesRequest stopRequest=new StopInstancesRequest().withInstanceIds(instanceId);
	ec2.stopInstances(stopRequest);
	System.out.printf("%s 인스턴스 중지 완료",instanceId);
}
public static void CreateInstances() {
	System.out.println("Create instances");
	Scanner menu=new Scanner(System.in);
	System.out.println("사용할 이미지를 입력하세요 : ");
	String imageId=menu.nextLine();
	System.out.println("사용할 Key를 입력하세요 : ");
	String keyName=menu.nextLine();
	//String instanceType=menu.nextLine();
	
	try {
		RunInstancesRequest run_request = new RunInstancesRequest()
			    .withImageId(imageId)
			    .withInstanceType(InstanceType.T2Micro)
			    .withKeyName(keyName)
			    .withMaxCount(1)
			    .withMinCount(1);
			RunInstancesResult run_response = ec2.runInstances(run_request);
			
			String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();
			
			System.out.printf("%s 이미지를 사용하여 %s 인스턴스 생성 완료",imageId,reservation_id);
	}
	catch (Exception e) {
		throw new AmazonClientException("인스턴스 생성에 필요한 인자를 잘못 입력", e);
	}
}

public static void ImageList() {
	String owner = "194836130746";

	DescribeImagesRequest request = new DescribeImagesRequest().withOwners(owner);
	Collection<Image> images = ec2.describeImages(request).getImages();
	for (Image img : images) {
		System.out.printf("[ImageID] %s, [Name] %s,[State] %s, [Owner] %s",img.getImageId(),
				img.getName(),img.getState(),img.getOwnerId());
		System.out.println();

	}
}

public static void TerminateInstances() {
	System.out.println("Terminate instances");
	Scanner menu=new Scanner(System.in);
	
	System.out.println("Instance 목록");
	System.out.println("------------------------------------------------------");
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
			System.out.println("------------------------------------------------------");
			done = true;
		}
	}
	System.out.println("삭제할 인스턴스의 id : ");
	String instanceName=menu.nextLine();
	
	TerminateInstancesRequest tRequest = new TerminateInstancesRequest().withInstanceIds(instanceName);
	ec2.terminateInstances(tRequest);
	
	System.out.printf("%s 인스턴스 삭제 완료",instanceName);
}
}

