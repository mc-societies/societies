package org.societies;

import com.google.common.io.Files;
import com.google.inject.Inject;
import net.catharos.lib.core.uuid.UUIDStorage;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.societies.api.Saveguard;
import org.societies.database.json.GroupMapper;
import org.societies.database.json.MemberMapper;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberProvider;

import java.io.*;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Represents a DefaultSaveguard
 */
//todo cities, sieges
public class DefaultSaveguard implements Saveguard {

    private final GroupMapper groupMapper;
    private final MemberMapper memberMapper;
    private final MemberProvider memberProvider;
    private final GroupProvider groupProvider;

    private final Logger logger;

    @Inject
    public DefaultSaveguard(GroupMapper groupMapper,
                            MemberMapper memberMapper,
                            MemberProvider memberProvider,
                            GroupProvider groupProvider, Logger logger) {
        this.groupMapper = groupMapper;
        this.memberMapper = memberMapper;
        this.memberProvider = memberProvider;
        this.groupProvider = groupProvider;
        this.logger = logger;
    }


    @Override
    public void backup(File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        backup(outputStream);

        outputStream.close();
    }

    @Override
    public void backup(OutputStream outputStream) throws IOException {
        File temp = Files.createTempDir();

        final UUIDStorage groupStorage = new UUIDStorage(new File(temp, "groups"), "json");
        final UUIDStorage memberStorage = new UUIDStorage(new File(temp, "members"), "json");

        final Set<Group> groups = groupProvider.getGroups();


        for (Group group : groups) {
            try {
                groupMapper.writeGroup(group, groupStorage.getFile(group.getUUID()));
            } catch (IOException e) {
                logger.catching(e);
            }
        }

        Set<Member> members = memberProvider.getMembers();

        for (Member member : members) {
            try {
                memberMapper.writeMember(member, memberStorage.getFile(member.getUUID()));
            } catch (IOException e) {
                logger.catching(e);
            }
        }


        writeZipFile(temp, outputStream);
    }

    public static void writeZipFile(File directory, OutputStream os) throws IOException {
        ZipOutputStream zip = new ZipOutputStream(os);

        for (File file : Files.fileTreeTraverser().preOrderTraversal(directory)) {
            if (file.isDirectory()) {
                continue;
            }

            String path = file.getAbsolutePath().substring(directory.getAbsolutePath().length());

            ZipEntry entry = new ZipEntry(path);
            zip.putNextEntry(entry);

            FileInputStream input = new FileInputStream(file);
            IOUtils.copy(input, zip);
            input.close();
        }


        zip.close();
        os.close();
    }
}
