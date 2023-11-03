package io.kinescope.sdk.player;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.mp4.PsshAtomUtil;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.XmlPullParserUtil;
import com.google.common.base.Ascii;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.UUID;

public class KinescopeDashManifestParser extends DashManifestParser {
    private static final String TAG = "KinescopeParser";

    @NonNull
    @Override
    protected Pair<String, DrmInitData.SchemeData> parseContentProtection(
            XmlPullParser xpp) throws XmlPullParserException, IOException {

        String schemeType = null;
        String licenseServerUrl = null;
        byte[] data = null;
        UUID uuid = null;

        String schemeIdUri = xpp.getAttributeValue(null, "schemeIdUri");
        if (schemeIdUri != null) {
            switch (Ascii.toLowerCase(schemeIdUri)) {
                case "urn:mpeg:dash:mp4protection:2011":
                    schemeType = xpp.getAttributeValue(null, "value");
                    String defaultKid = XmlPullParserUtil.getAttributeValueIgnorePrefix(xpp, "default_KID");
                    if (!TextUtils.isEmpty(defaultKid)
                            && !"00000000-0000-0000-0000-000000000000".equals(defaultKid)) {
                        String[] defaultKidStrings = defaultKid.split("\\s+");
                        UUID[] defaultKids = new UUID[defaultKidStrings.length];
                        for (int i = 0; i < defaultKidStrings.length; i++) {
                            defaultKids[i] = UUID.fromString(defaultKidStrings[i]);
                        }
                        data = PsshAtomUtil.buildPsshAtom(C.COMMON_PSSH_UUID, defaultKids, null);
                        uuid = C.COMMON_PSSH_UUID;
                    }
                    break;
                case "urn:uuid:9a04f079-9840-4286-ab92-e65be0885f95":
                    uuid = C.PLAYREADY_UUID;
                    break;
                case "urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed":
                    uuid = C.WIDEVINE_UUID;
                    break;
                case "urn:uuid:e2719d58-a985-b3c9-781a-b030af78d30e":
                    uuid = C.CLEARKEY_UUID;
                    break;
                default:
                    break;
            }
        }

        do {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "clearkey:Laurl") && xpp.next() == XmlPullParser.TEXT) {
                licenseServerUrl = xpp.getText();
            } else if (XmlPullParserUtil.isStartTag(xpp, "ms:laurl")) {
                licenseServerUrl = xpp.getAttributeValue(null, "licenseUrl");
            }else if (XmlPullParserUtil.isStartTag(xpp, "dashif:Laurl") && xpp.next() == XmlPullParser.TEXT) {
                licenseServerUrl = xpp.getText();
            } else if (data == null
                    && XmlPullParserUtil.isStartTagIgnorePrefix(xpp, "pssh")
                    && xpp.next() == XmlPullParser.TEXT) {
                // The cenc:pssh element is defined in 23001-7:2015.
                data = Base64.decode(xpp.getText(), Base64.DEFAULT);
                uuid = PsshAtomUtil.parseUuid(data);
                if (uuid == null) {
                    Log.w(TAG, "Skipping malformed cenc:pssh data");
                    data = null;
                }
            } else if (data == null
                    && C.PLAYREADY_UUID.equals(uuid)
                    && XmlPullParserUtil.isStartTag(xpp, "mspr:pro")
                    && xpp.next() == XmlPullParser.TEXT) {
                // The mspr:pro element is defined in DASH Content Protection using Microsoft PlayReady.
                data =
                        PsshAtomUtil.buildPsshAtom(
                                C.PLAYREADY_UUID, Base64.decode(xpp.getText(), Base64.DEFAULT));
            } else {
                maybeSkipTag(xpp);
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "ContentProtection"));

        DrmInitData.SchemeData schemeData =
                uuid != null ? new DrmInitData.SchemeData(uuid, licenseServerUrl, MimeTypes.VIDEO_MP4, data) : null;
        return Pair.create(schemeType, schemeData);
    }
}
