package cn.lary.pkg.srs.callback;

import cn.lary.pkg.srs.dto.OnPlayDTO;
import cn.lary.pkg.srs.dto.OnPublishDTO;
import cn.lary.pkg.srs.dto.OnStopDTO;
import cn.lary.pkg.srs.dto.OnUnpublishDTO;

public interface SrsCallback {

    /**
     *   when client(encoder) publish to vhost/app/stream, call the hook,
     * @param dto {@link OnPublishDTO}
     * @return 0
     */
    int onPublish(OnPublishDTO dto);

    /**
     * when client(encoder) stop publish to vhost/app/stream, call the hook,
     * @param dto {@link OnUnpublishDTO}
     * @return 0
     */
    int onUnPublish(OnUnpublishDTO dto);

    /**
     * # when client start to play vhost/app/stream, call the hook,
     * @param dto {@link OnPlayDTO}
     * @return 0
     */
    int onPlay(OnPlayDTO dto);

    /**
     * when client stop to play vhost/app/stream, call the hook
     * @param dto {@link OnStopDTO}
     * @return 0
     */
    int onStop(OnStopDTO dto);
}
