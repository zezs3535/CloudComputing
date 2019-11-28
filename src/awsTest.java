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
			credentialsProvider.getCredentials();	//.aws폴더에 있는 credentials 파일을 참조하여 Key값을 받아온다.
		} catch (Exception e) {						//참조하지 못할경우 예외처리
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (~/.aws/credentials), and is in valid format.", e);

		}
		ec2 = AmazonEC2ClientBuilder.standard().withCredentials(credentialsProvider).withRegion("us-east-1") 
				.build();		//AmazonEC2ClientBuilder.standard()으로 기본값을 설정한 새로운 인스턴스를 생성한다.
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
		System.out.println(" 9. terminate instance 99. quit");
		System.out.println("------------------------------------------------------------");
		System.out.print("Enter an integer: ");
		int number = menu.nextInt();
		switch(number) {
		case 1:
			System.out.println(); 
			listInstances();		//인스턴스 목록 출력
			break;
		case 2:
			System.out.println(); 
			AvailableZones();		//가용 영역
			break;
		case 3:
			System.out.println(); 
			StartInstances();		//인스턴스 실행
			break;
		case 4:
			System.out.println();
			AvailableRegions();		//가용 지역
			break;
		case 5:
			System.out.println();
			StopInstances();		//인스턴스 중지
			break;
		case 6:
			System.out.println();
			CreateInstances();		//인스턴스 생성
			break;
		case 7:
			System.out.println();
			RebootInstances();		//인스턴스 재시작
			break;
		case 8:
			System.out.println();
			ImageList();			//이미지 목록 출력
			break;
		case 9:
			System.out.println();
			TerminateInstances();	//인스턴스 삭제
			break;
		case 99:
			System.exit(0);			//99값이 들어오면 종료
		}
	}
}

public static void listInstances() {	//인스턴스 목록 출력
		System.out.println("Listing instances....");
		boolean done = false;			//while문을 제어하는 변수
		DescribeInstancesRequest request = new DescribeInstancesRequest();	//AWS SDK의 클래스를 이용하여 객체를 생성한다.
		while (!done) {	
			DescribeInstancesResult response = ec2.describeInstances(request);	//요청 작업에 대한 반환된 데이터를 표시하는 객체
			for (Reservation reservation : response.getReservations()) { // iterator를 사용하여 인스턴스를 출력
				for (Instance instance : reservation.getInstances()) {	// iterator를 사용하여 인스턴스의 정보를 출력
					System.out.printf(
							"[id] %s, " + "[AMI] %s, " + "[type] %s, " + "[state] %10s, " + "[monitoring state] %s",
							instance.getInstanceId(), instance.getImageId(), instance.getInstanceType(),
							instance.getState().getName(), instance.getMonitoring().getState());
				}
				System.out.println();
			}
			request.setNextToken(response.getNextToken());	
			if (response.getNextToken() == null) {	//다음 인스턴스가 없으면 끝
				System.out.println("끝!");
				done = true;
			}
		}
}

public static void AvailableZones() {	//가용 영역
	System.out.println("Show AvailableZones");
	DescribeAvailabilityZonesResult zRequest = ec2.describeAvailabilityZones();	//AWS SDK의 클래스를 이용하여 객체를 생성한다.
	for(AvailabilityZone zone:zRequest.getAvailabilityZones()) {	//iterator를 사용하여 인스턴스들을 거쳐감
		System.out.printf("[Zone name : %s]"+"[Zone id : %s]",zone.getZoneName(),zone.getZoneId());
		System.out.println();
	}
}
public static void StartInstances() {	//인스턴스 실행
	System.out.println("Start instances");
	Scanner menu = new Scanner(System.in);	//입력을 받기 위한 스캐너
	System.out.print("시작할 인스턴스의 id를 입력하세요 : ");
	String instanceId=menu.nextLine();	//인스턴스 이름을 입력받는다.
	StartInstancesRequest sRequest=new StartInstancesRequest().withInstanceIds(instanceId);	//인스턴스 실행에 필요한 Id를 받은 객체 생성
	ec2.startInstances(sRequest);	//인스턴스 실행 시작
	System.out.printf("%s 인스턴스 시작 완료",instanceId);
}
public static void AvailableRegions() {	//가용 지역
	System.out.println("Show AvailableRegions");
	DescribeRegionsResult rRequest = ec2.describeRegions();	//가용 지역에 관한 객체를 생성한다.
	for(Region region: rRequest.getRegions()) {	//iterator를 통해 객체의 가용 지역들을 탐색.
		System.out.printf(region.getRegionName());
		System.out.println();
	}
}
public static void StopInstances() {	//인스턴스 중지
	System.out.println("Stop instances");
	Scanner menu=new Scanner(System.in);
	System.out.print("중지할 인스턴스의 id를 입력하세요 : ");
	String instanceId=menu.nextLine();
	StopInstancesRequest stopRequest=new StopInstancesRequest().withInstanceIds(instanceId);	//인스턴스 중지에 필요한 Id를 받은 객체 생성
	ec2.stopInstances(stopRequest);	//인스턴스 중지 시작
	System.out.printf("%s 인스턴스 중지 완료",instanceId);
}
public static void CreateInstances() {	//인스턴스 생성
	System.out.println("Create instances");
	Scanner menu=new Scanner(System.in);
	System.out.println("사용할 이미지를 입력하세요 : ");
	String imageId=menu.nextLine();
	System.out.println("사용할 Key를 입력하세요 : ");
	String keyName=menu.nextLine();
	
	try {
		RunInstancesRequest run_request = new RunInstancesRequest()	//생성에 필요한 이미지ID와 인스턴스 타입, 키 네임을 받아서 생성에 필요한 객체 생성
			    .withImageId(imageId)
			    .withInstanceType(InstanceType.T2Micro)
			    .withKeyName(keyName)
			    .withMaxCount(1)
			    .withMinCount(1);
			RunInstancesResult run_response = ec2.runInstances(run_request);
			
			String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();	//생성한 객체의 Id를 받아온다
			
			System.out.printf("%s 이미지를 사용하여 %s 인스턴스 생성 완료",imageId,reservation_id);
	}
	catch (Exception e) {
		throw new AmazonClientException("인스턴스 생성에 필요한 인자를 잘못 입력", e);
	}
}
public static void RebootInstances() {	//인스턴스 재시작
	Scanner menu=new Scanner(System.in);
	System.out.println("재시작 할 인스턴스의 id를 입력하세요 : ");
	String instanceId=menu.nextLine();
	
	RebootInstancesRequest request=new RebootInstancesRequest().withInstanceIds(instanceId);	//재시작 할 인스턴스의 Id를 받아서 객체 생성
	RebootInstancesResult response = ec2.rebootInstances(request);	//객체 재시작 실행
	System.out.printf("%s 인스턴스 리부팅 완료 ",instanceId);
}
public static void ImageList() {
	System.out.println("Show ImageList");
	String owner = "194836130746";	//이미지의 소유자 번호 (중복을 막기 위함)

	DescribeImagesRequest request = new DescribeImagesRequest().withOwners(owner);	//소유자 번호를 가지는 객체 생성
	Collection<Image> images = ec2.describeImages(request).getImages();	//소유자 번호의 image내용을 담고 있는 객체 생성
	for (Image img : images) {	//iterator를 통해 객체들 탐색
		System.out.printf("[ImageID] %s, [Name] %s,[State] %s, [Owner] %s",img.getImageId(),
				img.getName(),img.getState(),img.getOwnerId());
		System.out.println();

	}
}

public static void TerminateInstances() {	//인스턴스 삭제
	System.out.println("Terminate instances");
	Scanner menu=new Scanner(System.in);
	
	System.out.println("Instance 목록");		//삭제 전 인스턴스 목록을 보여줌
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
	
	TerminateInstancesRequest tRequest = new TerminateInstancesRequest().withInstanceIds(instanceName);	//삭제 할 인스턴스의 이름을 넣은 객체
	ec2.terminateInstances(tRequest);
	
	System.out.printf("%s 인스턴스 삭제 완료",instanceName);
}
}