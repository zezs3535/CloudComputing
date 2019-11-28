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
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.lightsail.model.RebootInstanceResult;
public class awsTest {

	static AmazonEC2 ec2;

	private static void init() throws Exception {
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();	//.aws������ �ִ� credentials ������ �����Ͽ� Key���� �޾ƿ´�.
		} catch (Exception e) {						//�������� ���Ұ�� ����ó��
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (~/.aws/credentials), and is in valid format.", e);

		}
		ec2 = AmazonEC2ClientBuilder.standard().withCredentials(credentialsProvider).withRegion("us-east-1") 
				.build();		//AmazonEC2ClientBuilder.standard()���� �⺻���� ������ ���ο� �ν��Ͻ��� �����Ѵ�.
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
		System.out.println(" 2015041032 ������ ");
		System.out.println(" Chungbuk National University");
		System.out.println("------------------------------------------------------------");
		System.out.println(" 1. list instance 2. available zones ");
		System.out.println(" 3. start instance 4. available regions ");
		System.out.println(" 5. stop instance 6. create instance ");
		System.out.println(" 7. reboot instance 8. list images ");
		System.out.println(" 9. terminate instance 99. quit");
		System.out.println("------------------------------------------------------------");
		System.out.print("Enter an integer: ");
		int number = menu.nextInt();
		switch(number) {
		case 1:
			System.out.println(); 
			listInstances();		//�ν��Ͻ� ��� ���
			break;
		case 2:
			System.out.println(); 
			AvailableZones();		//���� ����
			break;
		case 3:
			System.out.println(); 
			StartInstances();		//�ν��Ͻ� ����
			break;
		case 4:
			System.out.println();
			AvailableRegions();		//���� ����
			break;
		case 5:
			System.out.println();
			StopInstances();		//�ν��Ͻ� ����
			break;
		case 6:
			System.out.println();
			CreateInstances();		//�ν��Ͻ� ����
			break;
		case 7:
			System.out.println();
			RebootInstances();		//�ν��Ͻ� �����
			break;
		case 8:
			System.out.println();
			ImageList();			//�̹��� ��� ���
			break;
		case 9:
			System.out.println();
			TerminateInstances();	//�ν��Ͻ� ����
			break;
		case 99:
			System.exit(0);			//99���� ������ ����
		}
	}
}

public static void listInstances() {	//�ν��Ͻ� ��� ���
		System.out.println("Listing instances....");
		boolean done = false;			//while���� �����ϴ� ����
		DescribeInstancesRequest request = new DescribeInstancesRequest();	//AWS SDK�� Ŭ������ �̿��Ͽ� ��ü�� �����Ѵ�.
		while (!done) {	
			DescribeInstancesResult response = ec2.describeInstances(request);	//��û �۾��� ���� ��ȯ�� �����͸� ǥ���ϴ� ��ü
			for (Reservation reservation : response.getReservations()) { // iterator�� ����Ͽ� �ν��Ͻ��� ���
				for (Instance instance : reservation.getInstances()) {	// iterator�� ����Ͽ� �ν��Ͻ��� ������ ���
					System.out.printf(
							"[id] %s, " + "[AMI] %s, " + "[type] %s, " + "[state] %10s, " + "[monitoring state] %s",
							instance.getInstanceId(), instance.getImageId(), instance.getInstanceType(),
							instance.getState().getName(), instance.getMonitoring().getState());
				}
				System.out.println();
			}
			request.setNextToken(response.getNextToken());	
			if (response.getNextToken() == null) {	//���� �ν��Ͻ��� ������ ��
				System.out.println("��!");
				done = true;
			}
		}
}

public static void AvailableZones() {	//���� ����
	System.out.println("Show AvailableZones");
	DescribeAvailabilityZonesResult zRequest = ec2.describeAvailabilityZones();	//AWS SDK�� Ŭ������ �̿��Ͽ� ��ü�� �����Ѵ�.
	for(AvailabilityZone zone:zRequest.getAvailabilityZones()) {	//iterator�� ����Ͽ� �ν��Ͻ����� ���İ�
		System.out.printf("[Zone name : %s]"+"[Zone id : %s]",zone.getZoneName(),zone.getZoneId());
		System.out.println();
	}
}
public static void StartInstances() {	//�ν��Ͻ� ����
	System.out.println("Start instances");
	Scanner menu = new Scanner(System.in);	//�Է��� �ޱ� ���� ��ĳ��
	System.out.print("������ �ν��Ͻ��� id�� �Է��ϼ��� : ");
	String instanceId=menu.nextLine();	//�ν��Ͻ� �̸��� �Է¹޴´�.
	StartInstancesRequest sRequest=new StartInstancesRequest().withInstanceIds(instanceId);	//�ν��Ͻ� ���࿡ �ʿ��� Id�� ���� ��ü ����
	ec2.startInstances(sRequest);	//�ν��Ͻ� ���� ����
	System.out.printf("%s �ν��Ͻ� ���� �Ϸ�",instanceId);
}
public static void AvailableRegions() {	//���� ����
	System.out.println("Show AvailableRegions");
	DescribeRegionsResult rRequest = ec2.describeRegions();	//���� ������ ���� ��ü�� �����Ѵ�.
	for(Region region: rRequest.getRegions()) {	//iterator�� ���� ��ü�� ���� �������� Ž��.
		System.out.printf(region.getRegionName());
		System.out.println();
	}
}
public static void StopInstances() {	//�ν��Ͻ� ����
	System.out.println("Stop instances");
	Scanner menu=new Scanner(System.in);
	System.out.print("������ �ν��Ͻ��� id�� �Է��ϼ��� : ");
	String instanceId=menu.nextLine();
	StopInstancesRequest stopRequest=new StopInstancesRequest().withInstanceIds(instanceId);	//�ν��Ͻ� ������ �ʿ��� Id�� ���� ��ü ����
	ec2.stopInstances(stopRequest);	//�ν��Ͻ� ���� ����
	System.out.printf("%s �ν��Ͻ� ���� �Ϸ�",instanceId);
}
public static void CreateInstances() {	//�ν��Ͻ� ����
	System.out.println("Create instances");
	Scanner menu=new Scanner(System.in);
	System.out.println("����� �̹����� �Է��ϼ��� : ");
	String imageId=menu.nextLine();
	System.out.println("����� Key�� �Է��ϼ��� : ");
	String keyName=menu.nextLine();
	
	try {
		RunInstancesRequest run_request = new RunInstancesRequest()	//������ �ʿ��� �̹���ID�� �ν��Ͻ� Ÿ��, Ű ������ �޾Ƽ� ������ �ʿ��� ��ü ����
			    .withImageId(imageId)
			    .withInstanceType(InstanceType.T2Micro)
			    .withKeyName(keyName)
			    .withMaxCount(1)
			    .withMinCount(1);
			RunInstancesResult run_response = ec2.runInstances(run_request);
			
			String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();	//������ ��ü�� Id�� �޾ƿ´�
			
			System.out.printf("%s �̹����� ����Ͽ� %s �ν��Ͻ� ���� �Ϸ�",imageId,reservation_id);
	}
	catch (Exception e) {
		throw new AmazonClientException("�ν��Ͻ� ������ �ʿ��� ���ڸ� �߸� �Է�", e);
	}
}
public static void RebootInstances() {	//�ν��Ͻ� �����
	Scanner menu=new Scanner(System.in);
	System.out.println("����� �� �ν��Ͻ��� id�� �Է��ϼ��� : ");
	String instanceId=menu.nextLine();
	
	RebootInstancesRequest request=new RebootInstancesRequest().withInstanceIds(instanceId);	//����� �� �ν��Ͻ��� Id�� �޾Ƽ� ��ü ����
	RebootInstancesResult response = ec2.rebootInstances(request);	//��ü ����� ����
	System.out.printf("%s �ν��Ͻ� ������ �Ϸ� ",instanceId);
}
public static void ImageList() {
	System.out.println("Show ImageList");
	String owner = "194836130746";	//�̹����� ������ ��ȣ (�ߺ��� ���� ����)

	DescribeImagesRequest request = new DescribeImagesRequest().withOwners(owner);	//������ ��ȣ�� ������ ��ü ����
	Collection<Image> images = ec2.describeImages(request).getImages();	//������ ��ȣ�� image������ ��� �ִ� ��ü ����
	for (Image img : images) {	//iterator�� ���� ��ü�� Ž��
		System.out.printf("[ImageID] %s, [Name] %s,[State] %s, [Owner] %s",img.getImageId(),
				img.getName(),img.getState(),img.getOwnerId());
		System.out.println();

	}
}

public static void TerminateInstances() {	//�ν��Ͻ� ����
	System.out.println("Terminate instances");
	Scanner menu=new Scanner(System.in);
	
	System.out.println("Instance ���");		//���� �� �ν��Ͻ� ����� ������
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
	System.out.println("������ �ν��Ͻ��� id : ");
	String instanceName=menu.nextLine();
	
	TerminateInstancesRequest tRequest = new TerminateInstancesRequest().withInstanceIds(instanceName);	//���� �� �ν��Ͻ��� �̸��� ���� ��ü
	ec2.terminateInstances(tRequest);
	
	System.out.printf("%s �ν��Ͻ� ���� �Ϸ�",instanceName);
}
}