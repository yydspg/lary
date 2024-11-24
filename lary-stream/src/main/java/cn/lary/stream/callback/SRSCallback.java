package cn.lary.stream.callback;

import cn.lary.external.srs.dto.OnPlayDTO;
import cn.lary.external.srs.dto.OnPublishDTO;
import cn.lary.external.srs.dto.OnStopDTO;
import cn.lary.external.srs.dto.OnUnpublishDTO;

public interface SRSCallback {

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
