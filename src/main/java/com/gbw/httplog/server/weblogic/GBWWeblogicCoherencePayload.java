package com.gbw.httplog.server.weblogic;

import com.gbw.httplog.utils.FileUtils;
import com.gbw.httplog.utils.ProcessUtils;
import com.gbw.httplog.utils.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GBWWeblogicCoherencePayload {

    private static final Logger log = LoggerFactory.getLogger(GBWWeblogicCoherencePayload.class);

    public static GBWWeblogicCoherencePayloadEntry makePayload(String version,String payloadDir,String cmd) throws IOException {

        String cmdPath = String.format("%s/%s/run",payloadDir,version);
        String filePath = String.format("/opt/data/weblogic/coherence_%s.data",version);

        String[] cmds = new String[4];

        cmds[0] = "/bin/bash";
        cmds[1] = cmdPath;
        cmds[2] = filePath;
        cmds[3] = cmd;

        String proc = ProcessUtils.executeCommand(cmds);

        if(!TextUtils.isEmpty(proc))
            log.info(String.format("Make weblogic payload for version:%s,path:%s,proc:%s",version,filePath,proc));
        else
            proc = "none";

        if(!FileUtils.hasContent(filePath)){

            log.error(String.format("Make weblogic payload for version:%s,path:%s,proc:%s,payload:%s failed!",version,filePath,proc,payloadDir));

            return null;
        }

        return new GBWWeblogicCoherencePayloadEntry(version,filePath);
    }


}
