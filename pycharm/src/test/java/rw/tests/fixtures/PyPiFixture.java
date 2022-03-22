package rw.tests.fixtures;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.ConnectionOptions;
import org.mockserver.model.Header;
import org.mockserver.model.MediaType;
import rw.config.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static org.mockserver.model.BinaryBody.binary;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.Headers.headers;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.HttpStatusCode.OK_200;


public class PyPiFixture {
    private ClientAndServer mockServer;
    Integer port = 1080;

    public PyPiFixture() throws IllegalAccessException {
        FieldUtils.writeField(Config.get(), "pypiUrl", this.getUrl(), true);
    }

    public void start() {
        this.mockServer = ClientAndServer.startClientAndServer(this.port);
    }

    public void stop() {
        this.mockServer.stop();
    }

    public String getUrl() {
        return String.format("http://localhost:%s", this.port);
    }

    public void mockSimple() throws IOException {
        String body = Files.readString(Path.of(this.getClass().getClassLoader().getResource("simpleReloadium.html").getFile()));

        this.mockServer.when(
                        request()
                                .withMethod("GET")
                                .withPath("/simple/reloadium"))
                .respond(
                        response()
                                .withStatusCode(OK_200.code())
                                .withBody(body)
                );

        byte[] wheelBody = Files.readAllBytes(Path.of(this.getClass().getClassLoader().getResource("reloadium-0.7.13-cp39-cp39-manylinux_2_17_x86_64.manylinux2014_x86_64.whl").getFile()));

        String[] wheels = new String[]{
                "/packages/17/a3/f19a05dec48b134ff2d8a1517d486b4a808bc1240b5f3cbc3c94c00adab8/reloadium-0.7.12-cp36-cp36m-macosx_10_15_x86_64.whl",
                "/packages/53/be/53d62c05408ee925fc8a8fdce03bf35a9697bcc9604c3d21e3fb7be90f93/reloadium-0.7.12-cp36-cp36m-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "/packages/de/f0/c5b7f997486b00d1df45e9cdbe3391c869837154875cf151a51af76c1aa0/reloadium-0.7.12-cp36-cp36m-win_amd64.whl",
                "/packages/de/f0/c5b7f997486b00d1df45e9cdbe3391c869837154875cf151a51af76c1aa0/reloadium-0.7.12-cp36-cp36m-win32.whl",
                "/packages/30/e1/3d932efcbf3bdf77e071398eeeef09c3c7905726ec8a39bb96be21572527/reloadium-0.7.12-cp37-cp37m-macosx_10_15_x86_64.whl",
                "/packages/30/40/c024ab17b3efb04559a1efe775856b1018296f88128c8968188dc52dbf38/reloadium-0.7.12-cp37-cp37m-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "/packages/aa/31/2bdabef0d0a58f3c0902b2edfeaf7bd45360a93b83eb4c0eda17d70a5ccf/reloadium-0.7.12-cp37-cp37m-win_amd64.whl",
                "/packages/de/f0/c5b7f997486b00d1df45e9cdbe3391c869837154875cf151a51af76c1aa0/reloadium-0.7.12-cp37-cp37m-win32.whl",
                "/packages/2a/e7/4c5667782bc3a606c389c0db4669e01535ab973482d8eeed4ab94d9071f3/reloadium-0.7.12-cp38-cp38-macosx_10_15_x86_64.whl",
                "/packages/09/fe/aadfebf13cc29d5e458f6da6ae7e39246fc7f0566988f2b36d94acb3f203/reloadium-0.7.12-cp38-cp38-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "/packages/31/3d/fc08af6601a3c87e5865448d4aa88226d7535ab9e13b57fb028938fe0443/reloadium-0.7.12-cp38-cp38-win_amd64.whl",
                "/packages/de/f0/c5b7f997486b00d1df45e9cdbe3391c869837154875cf151a51af76c1aa0/reloadium-0.7.12-cp38-cp38-win32.whl",
                "/packages/b6/ce/28c5d8fe1ca780021bdaf12ae41ea0b6016e9d22914a369d4e358fd250ae/reloadium-0.7.12-cp39-cp39-macosx_10_15_x86_64.whl",
                "/packages/45/2f/ad446089f3b4bdf69fd9911a386fcd0de14aed0d8be6195c4d098d4b52e9/reloadium-0.7.12-cp39-cp39-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "/packages/9f/40/9de0e700da3ab14c248d744210fec04bad9777749141170ce1b72fb16b50/reloadium-0.7.12-cp39-cp39-win_amd64.whl",
                "/packages/de/f0/c5b7f997486b00d1df45e9cdbe3391c869837154875cf151a51af76c1aa0/reloadium-0.7.12-cp39-cp39-win32.whl",
                "/packages/b6/ce/28c5d8fe1ca780021bdaf12ae41ea0b6016e9d22914a369d4e358fd250ae/reloadium-0.7.12-cp310-cp310-macosx_10_15_x86_64.whl",
                "/packages/45/2f/ad446089f3b4bdf69fd9911a386fcd0de14aed0d8be6195c4d098d4b52e9/reloadium-0.7.12-cp310-cp310-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "/packages/9f/40/9de0e700da3ab14c248d744210fec04bad9777749141170ce1b72fb16b50/reloadium-0.7.12-cp310-cp310-win_amd64.whl",
                "/packages/de/f0/c5b7f997486b00d1df45e9cdbe3391c869837154875cf151a51af76c1aa0/reloadium-0.7.12-cp310-cp310-win32.whl",
                "/packages/3b/2d/03b09c72a92296185a4c8e4263962c89237323d63c0616ac3bffd3f6ceb8/reloadium-0.7.13-cp36-cp36m-macosx_10_15_x86_64.whl",
                "/packages/02/b5/b0e1a245ba7e8823e68a1e278589f1bceec6b6a79ae472fe6942f78247af/reloadium-0.7.13-cp36-cp36m-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "/packages/4a/74/116facc89b04ffb9a692a05efdd752ed4e43d69865f4c3627f6054ab2b9e/reloadium-0.7.13-cp36-cp36m-win_amd64.whl",
                "/packages/de/f0/c5b7f997486b00d1df45e9cdbe3391c869837154875cf151a51af76c1aa0/reloadium-0.7.13-cp36-cp36m-win32.whl",
                "/packages/8e/be/c3d39f375d425839b698ba5b3ab0cfa66b09ca16c5be92956fe0956ff9cf/reloadium-0.7.13-cp37-cp37m-macosx_10_15_x86_64.whl",
                "/packages/95/10/a32c1a24604c05600806ce8c7691c877ad00380d4eb838af11894a12cbf2/reloadium-0.7.13-cp37-cp37m-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "/packages/03/6d/8bbde32cd8883c397794cd223506b52d9679ee2c45c5e8d4a5b0677f715d/reloadium-0.7.13-cp37-cp37m-win_amd64.whl",
                "/packages/de/f0/c5b7f997486b00d1df45e9cdbe3391c869837154875cf151a51af76c1aa0/reloadium-0.7.13-cp37-cp37m-win32.whl",
                "/packages/9b/56/6b418dbb28924eb4ea742c8c83e58370884efb77634eac6e9cf40f0cb9ed/reloadium-0.7.13-cp38-cp38-macosx_10_15_x86_64.whl",
                "/packages/91/86/61b011f1e470a523280d3810a756532e3b2e0b36900f794bde78754137db/reloadium-0.7.13-cp38-cp38-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "/packages/68/d2/ed2c9c96ea5d425d076b3eb2b8d4881bdd3dfbc0de1dda784f0b50b5c394/reloadium-0.7.13-cp38-cp38-win_amd64.whl",
                "/packages/68/d2/ed2c9c96ea5d425d076b3eb2b8d4881bdd3dfbc0de1dda784f0b50b5c394/reloadium-0.7.13-cp38-cp38-win32.whl",
                "/packages/8c/bc/cfc4b06e2e9680b6da6b00ec257d5ea31404e6cfb890857bbe43f534e06c/reloadium-0.7.13-cp39-cp39-macosx_10_15_x86_64.whl",
                "/packages/b3/4e/e118b00b5019a2e014e67ccdf7f728bee580070c95cb14b714284bcb24f9/reloadium-0.7.13-cp39-cp39-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "/packages/67/1b/36ccabd6a799014077eb5cd6558b3f6ccd1202b1858a4ca4259cb0c2e9d9/reloadium-0.7.13-cp39-cp39-win_amd64.whl",
                "/packages/de/f0/c5b7f997486b00d1df45e9cdbe3391c869837154875cf151a51af76c1aa0/reloadium-0.7.13-cp39-cp39-win32.whl",
                "/packages/b6/ce/28c5d8fe1ca780021bdaf12ae41ea0b6016e9d22914a369d4e358fd250ae/reloadium-0.7.13-cp310-cp310-macosx_10_15_x86_64.whl",
                "/packages/45/2f/ad446089f3b4bdf69fd9911a386fcd0de14aed0d8be6195c4d098d4b52e9/reloadium-0.7.13-cp310-cp310-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "/packages/9f/40/9de0e700da3ab14c248d744210fec04bad9777749141170ce1b72fb16b50/reloadium-0.7.13-cp310-cp310-win_amd64.whl",
                "/packages/de/f0/c5b7f997486b00d1df45e9cdbe3391c869837154875cf151a51af76c1aa0/reloadium-0.7.13-cp310-cp310-win32.whl",
        };

        for (String w : wheels) {
            this.mockServer.when(
                            request()
                                    .withPath(w))
                    .respond(
                            response()
                                    .withStatusCode(OK_200.code())
                                    .withReasonPhrase(OK_200.reasonPhrase())
                                    .withConnectionOptions(ConnectionOptions.connectionOptions().withKeepAliveOverride(true))
                                    .withBody(binary(wheelBody))
                                    .withHeaders(header(CONTENT_TYPE.toString(), MediaType.APPLICATION_OCTET_STREAM.toString()),
                                            header(ACCEPT_ENCODING.toString(), "gzip, deflate"),
                                            header(CACHE_CONTROL.toString(), "must-revalidate, post-check=0, pre-check=0"))
                    );
        }

    }
}
