package dt176g.hehe0601.paint.rxClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import dt176g.hehe0601.paint.Shape;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 
 * This class handles the client side operations for the server/client
 * architecture.
 * 
 * @author Henrik Henriksson (hehe0601)
 * @since 2020-12-23
 *
 */

public class Client {

	public static final String DEFAULT_ADDRESS = "localhost";
	public static final int DEFAULT_PORT = 10000;

	private static final String DEFAULT_UDP_ADDR = "230.0.0.0";
	private static final int DEFAULT_UDP_PORT = 4446;
	private static final int BUF_SIZE = 4096;

	private String serverAddress;
	private int serverPort;

	private Socket socket = null;
	private MulticastSocket mSock = null;
	private InetAddress inetAddress = null;
	private InetSocketAddress group = null;
	private NetworkInterface netwrInterface = null;
	private Observable<Shape> outstreamObservable;
	private Observable<Shape> instreamObservable;

	public Client(String pServerAddress, int pServerport) {
		super();

		this.serverAddress = pServerAddress;
		this.serverPort = pServerport;
	}

	public Client() {
		this(DEFAULT_ADDRESS, DEFAULT_PORT);
	}

	public void setoutstreamObservable(Observable<Shape> pOutstreamObservable) {
		this.outstreamObservable = pOutstreamObservable;
	}

	public Observable<Shape> getinstreamObservable() {
		return instreamObservable;
	}

	public void attemptConnect() {
		try {
			socket = new Socket(serverAddress, serverPort);
			mSock = new MulticastSocket(DEFAULT_UDP_PORT);
			inetAddress = InetAddress.getByName(DEFAULT_UDP_ADDR);
			group = new InetSocketAddress(inetAddress, DEFAULT_UDP_PORT);
			netwrInterface = NetworkInterface.getByName("bge0");
			mSock.joinGroup(group, netwrInterface);

		} catch (UnknownHostException e) {
			System.err.println("Could not connect to serversocket, Unkown Host");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("I/O error connecting to host. Is server running?");
//			e.printStackTrace();
		}

		// send the socket:
		Observable<Socket> socketObs = Observable.just(socket);

		// convert the socket to an objectOutputStream, through a buffered outputstream.
		Observable<ObjectOutputStream> objectoutputstreamObs = socketObs.map(Socket::getOutputStream)
				.map(os -> new BufferedOutputStream(os)).map(bos -> new ObjectOutputStream(bos));

		// Write the latest shape to the outstream.
		outstreamObservable.withLatestFrom(objectoutputstreamObs, (shape, outStream) -> {
			outStream.writeObject(shape);
			outStream.flush();
			return outStream;
		}).subscribe(s -> {
			// print error message on error:
		}, e -> System.err.println("No Connection, cannot send shape to server"));

		// TCP from the Server:
		Observable<Shape> TCPinstreamObservable = socketObs.flatMap(s -> Observable.just(s)).map(Socket::getInputStream)
				.map(BufferedInputStream::new).map(ObjectInputStream::new).subscribeOn(Schedulers.io())
				.flatMap(ois -> Observable.create(emitter -> {
					try {
						while (true) {
							Object obj = ois.readObject();
							emitter.onNext((Shape) obj);
						}
					} catch (IOException e) {
						System.err.println("No more Objects to read");
					}
				}));

		// UDP from server
		Observable<Shape> UDPinstreamObservable = Observable.<MulticastSocket>just(mSock)
				.flatMap(mSock -> Observable.<Shape>create(emitter -> {
					try {
						while (true) {
							byte[] buf = new byte[BUF_SIZE];
							DatagramPacket packet = new DatagramPacket(buf, buf.length, inetAddress, DEFAULT_UDP_PORT);
							mSock.receive(packet);
							ByteArrayInputStream in = new ByteArrayInputStream(packet.getData());
							ObjectInputStream is = new ObjectInputStream(in);
							Object readObject = is.readObject();
							emitter.onNext((Shape) readObject);
						}

					} catch (SocketException e) {
						System.err.println("Connection Reset");
					}
				}).subscribeOn(Schedulers.io()));

		instreamObservable = Observable.merge(TCPinstreamObservable, UDPinstreamObservable).publish().autoConnect();
	}

	// close the socket.
	public void attemptDisconnect() {
		try {
			socket.close();
			mSock.leaveGroup(group, netwrInterface);
			mSock.close();

		} catch (Exception e) {
			System.err.println("Error Closing Socket");
		}
		socket = null;
		mSock = null;
		inetAddress = null;
		group = null;
		netwrInterface = null;
	}

}
