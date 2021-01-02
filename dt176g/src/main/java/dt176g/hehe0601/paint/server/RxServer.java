package dt176g.hehe0601.paint.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import dt176g.hehe0601.paint.ClearRequest;
import dt176g.hehe0601.paint.Shape;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 
 * This class handles the connection of clients to the paint server, as well as
 * sending and receiving updates to the drawing area in each client.
 * 
 * @author Henrik Henriksson (hehe0601)
 * @since 2020-12-23
 *
 */

public class RxServer {

	private static final int DEFAULT_PORT = 10000;
	private int port;

	private static final String DEFAULT_UDP_ADDR = "230.0.0.0";
	private static final int DEFAULT_UDP_PORT = 4446;

	private ServerSocket ssock = null;
	// For UDP multicasting to one or more clients.
	private MulticastSocket mSock = null;

	private InetAddress inetAddress = null;
	private InetSocketAddress group = null;
	private NetworkInterface netwrInterface = null;

	// storage for the shapes previously sent to the server
	private ConcurrentLinkedQueue<Shape> storedDrawing = null;

	public RxServer(int pPort) {

		this.port = pPort;
		storedDrawing = new ConcurrentLinkedQueue<>();

		launchServer();
	}

	private void launchServer() {
		try {
			ssock = new ServerSocket(this.port);
			mSock = new MulticastSocket(DEFAULT_UDP_PORT);
			inetAddress = InetAddress.getByName(DEFAULT_UDP_ADDR);
			group = new InetSocketAddress(inetAddress, DEFAULT_UDP_PORT);
			netwrInterface = NetworkInterface.getByName("bge0");
			mSock.joinGroup(group, netwrInterface);
		} catch (Exception e) {
			System.err.println("Error initializing server sockets!");
			e.printStackTrace();
		}

		System.out.println("Server started on Port: " + port);

		// Create observable emitting acceptances and starting new clienthandlers for
		// each individual client.
		Observable.<Socket>create(e -> {
			while (true) {
				e.onNext(ssock.accept());
			}
		}).doOnNext(ss -> System.out
				.println("\nNew client connected from " + ss.getInetAddress().getHostAddress() + ":" + ss.getPort()))
				.doOnNext(socket -> clientHandler(Observable.just(socket))).subscribe(s -> {
				}, Throwable::printStackTrace);

	}

	private void clientHandler(@NonNull Observable<Socket> justSockObs) {
		// As a client connects, send any previous history to that client:
		sendDrawingHistory(justSockObs);

		// Establish an inputstream to read incomming shapes from the client(s). Set to
		// multicast on separate thread.
		justSockObs.map(Socket::getInputStream).map(is -> new BufferedInputStream(is))
				.map(bis -> new ObjectInputStream(bis)).flatMap(ois -> Observable.<Shape>create(emitter -> {
					while (true) {
						try {
							Shape pShape = (Shape) ois.readObject();

							emitter.onNext(pShape);
							if (pShape instanceof ClearRequest) {
								storedDrawing.clear();
							}

						} catch (IOException e) {
							// Exits the loop when no more object can be read
						}
					}
				})).subscribeOn(Schedulers.io()).subscribe(this::sendMulticast, e -> {
					System.err.println("Connection Reset. Host Disconnected");
				});

	}

	/*
	 * Send a received shape on multicast to any and all clients using UDP
	 */
	private void sendMulticast(Shape pShape) {

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oos;
			oos = new ObjectOutputStream(out);
			oos.writeObject(pShape);
			System.out.println("Emitting Shape on UDP");
			byte[] buf = out.toByteArray();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, inetAddress, DEFAULT_UDP_PORT);
			storedDrawing.add(pShape);
			mSock.send(packet);

		} catch (IOException e) {
			System.err.println("Error Multicasting recieved shape");
			e.printStackTrace();
		}

	}

	private void sendDrawingHistory(Observable<Socket> pSock) {
		// Create observable emitting an ObjectOutputStream
		Observable<ObjectOutputStream> oosObs = pSock.map(Socket::getOutputStream)
				.map(os -> new BufferedOutputStream(os)).map(bos -> new ObjectOutputStream(bos));

		// Combine emitted shapes and outStream and send shapes back to client
		Observable.fromIterable(storedDrawing).withLatestFrom(oosObs, (shape, outStream) -> {
			outStream.writeObject(shape);
			outStream.flush();
			return outStream;
		}).subscribe();

	}

	public static void main(String[] args) {
		int port = DEFAULT_PORT;

		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.err.println("Error Setting port. Invalid argument.");
			}
		}
		new RxServer(port);

	}
}
