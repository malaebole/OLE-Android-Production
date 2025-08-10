package ae.oleapp.webrtc;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ae.oleapp.voiceutils.DataModel;
import ae.oleapp.voiceutils.DataModelType;

public class WebRTCClient {
    private final Gson gson = new Gson();
    private final Context context;
    private final String username;
    private final EglBase.Context eglBaseContext = EglBase.create().getEglBaseContext();
    private final PeerConnectionFactory peerConnectionFactory;
    private final PeerConnection peerConnection;
    private final List<PeerConnection.IceServer> iceServer = new ArrayList<>();
    private final AudioSource localAudioSource;
    private final String localTrackId = "local_track";
    private final String localStreamId = "local_stream";
    private AudioTrack localAudioTrack;
    private MediaStream localStream;
    private final MediaConstraints mediaConstraints = new MediaConstraints();

    public Listener listener;

    private final RTCAudioManager rtcAudioManager;
    private final boolean isAudioTrackEnabled = true;

    public WebRTCClient(Context context, PeerConnection.Observer observer, String username) {
        this.context = context;
        this.username = username;
        initPeerConnectionFactory();
        peerConnectionFactory = createPeerConnectionFactory();
        iceServer.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());
        peerConnection = createPeerConnection(observer);
        localAudioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        startLocalAudioStreaming();
        rtcAudioManager = RTCAudioManager.create(context);
        rtcAudioManager.start(new RTCAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(RTCAudioManager.AudioDevice selectedAudioDevice, Set<RTCAudioManager.AudioDevice> availableAudioDevices) {
                // Handle audio device changes if needed
            }
        });
//        Log.d("TAG", "peerConnectionFactory: " + peerConnectionFactory);
//        Log.d("TAG", "peerConnection: " + peerConnection);
//        Log.d("TAG", "localAudioSource: " + localAudioSource);
//        Log.d("TAG", "rtcAudioManager: " + rtcAudioManager);


    }

    // Initializing peer connection section
    private void initPeerConnectionFactory() {
        PeerConnectionFactory.InitializationOptions options = PeerConnectionFactory.InitializationOptions.builder(context)
                .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
                .setEnableInternalTracer(true)
                .createInitializationOptions();
        PeerConnectionFactory.initialize(options);
        Log.d("TAG", "peerConnectionFactoryOptions: " + options);
    }

    private PeerConnectionFactory createPeerConnectionFactory() {
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        options.disableEncryption = false;
        options.disableNetworkMonitor = false;
        return PeerConnectionFactory.builder()
                .setVideoEncoderFactory(new DefaultVideoEncoderFactory(eglBaseContext, true, true))
                .setVideoDecoderFactory(new DefaultVideoDecoderFactory(eglBaseContext))
                .setOptions(options).createPeerConnectionFactory();
    }

    private PeerConnection createPeerConnection(PeerConnection.Observer observer) {
        return peerConnectionFactory.createPeerConnection(iceServer, observer);

    }

    // Initializing UI like surface view renderers
//    public void initSurfaceViewRenderer(SurfaceViewRenderer viewRenderer) {
//        viewRenderer.setEnableHardwareScaler(true);
//        viewRenderer.setMirror(true);
//        viewRenderer.init(eglBaseContext, null);
//    }

//    public void initLocalSurfaceView(SurfaceViewRenderer view) {
//        initSurfaceViewRenderer(view);
//        startLocalAudioStreaming();
//    }

    public void startLocalAudioStreaming() {
//        SurfaceTextureHelper helper = SurfaceTextureHelper.create(Thread.currentThread().getName(), eglBaseContext);



        localAudioTrack = peerConnectionFactory.createAudioTrack(localTrackId + "_audio", localAudioSource);
        localStream = peerConnectionFactory.createLocalMediaStream(localStreamId);
        localStream.addTrack(localAudioTrack);
        peerConnection.addStream(localStream);
//
//        Log.d("TAG", "localAudioTrack: " + localAudioTrack);
//        Log.d("TAG", "localStream: " + localStream);
//        Log.d("TAG", "peerConnection: " + peerConnection);


    }

//    public void initRemoteSurfaceView(SurfaceViewRenderer view) {
//        initSurfaceViewRenderer(view);
//    }

    // Negotiation section like call and answer
    public void call(String target) {
        try {
            peerConnection.createOffer(new MySdpObserver() {
                @Override
                public void onCreateSuccess(SessionDescription sessionDescription) {
                    super.onCreateSuccess(sessionDescription);
                    peerConnection.setLocalDescription(new MySdpObserver() {
                        @Override
                        public void onSetSuccess() {
                            super.onSetSuccess();
                            // It's time to transfer this SDP to the other peer
                            if (listener != null) {
                                listener.onTransferDataToOtherPeer(new DataModel(target, username, sessionDescription.description, DataModelType.Offer));
                                Log.d("TAG", "Offer: " + target + username + sessionDescription.description + DataModelType.Offer);

                            }
                        }
                    }, sessionDescription);
                }
            }, mediaConstraints);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void answer(String target) {
        try {
            peerConnection.createAnswer(new MySdpObserver() {
                @Override
                public void onCreateSuccess(SessionDescription sessionDescription) {
                    super.onCreateSuccess(sessionDescription);
                    peerConnection.setLocalDescription(new MySdpObserver() {
                        @Override
                        public void onSetSuccess() {
                            super.onSetSuccess();
                            // It's time to transfer this SDP to the other peer
                            if (listener != null) {
                                listener.onTransferDataToOtherPeer(new DataModel(target, username, sessionDescription.description, DataModelType.Answer));
                                Log.d("TAG", "Answer: " + target + username + sessionDescription.description + DataModelType.Answer);

                            }
                        }
                    }, sessionDescription);
                }
            }, mediaConstraints);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRemoteSessionReceived(SessionDescription sessionDescription) {
        peerConnection.setRemoteDescription(new MySdpObserver(), sessionDescription);
    }

    public void addIceCandidate(IceCandidate iceCandidate) {
        peerConnection.addIceCandidate(iceCandidate);
        Log.d("TAG", "IceCandidateAdded: " + iceCandidate);

    }

    public void sendIceCandidate(IceCandidate iceCandidate, String target) {
        addIceCandidate(iceCandidate);
        if (listener != null) {
            listener.onTransferDataToOtherPeer(new DataModel(target, username, gson.toJson(iceCandidate), DataModelType.IceCandidate));
            Log.d("TAG", "IceCandidate: " + target + username + gson.toJson(iceCandidate) + DataModelType.IceCandidate);

        }
    }

    public void toggleAudio(Boolean isEnabled) {
        if (localAudioTrack != null) {
            localAudioTrack.setEnabled(isEnabled);
        }


    }

    public void closeConnection() {
        try {
//            localAudioTrack.dispose();
            if (localAudioTrack != null) {
                localAudioTrack.dispose();
                localAudioTrack = null; // Set it to null after disposing
            }
            peerConnection.close();
            Log.d("TAG", "ConnectionClosed");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface Listener {
        void onTransferDataToOtherPeer(DataModel model);
    }
}