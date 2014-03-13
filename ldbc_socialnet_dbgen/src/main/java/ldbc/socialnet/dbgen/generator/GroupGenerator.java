/*
 * Copyright (c) 2013 LDBC
 * Linked Data Benchmark Council (http://ldbc.eu)
 *
 * This file is part of ldbc_socialnet_dbgen.
 *
 * ldbc_socialnet_dbgen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ldbc_socialnet_dbgen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ldbc_socialnet_dbgen.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2011 OpenLink Software <bdsmt@openlinksw.com>
 * All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation;  only Version 2 of the License dated
 * June 1991.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package ldbc.socialnet.dbgen.generator;

import java.util.TreeSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import ldbc.socialnet.dbgen.dictionary.LocationDictionary;
import ldbc.socialnet.dbgen.dictionary.TagDictionary;
import ldbc.socialnet.dbgen.objects.Friend;
import ldbc.socialnet.dbgen.objects.Group;
import ldbc.socialnet.dbgen.objects.GroupMemberShip;
import ldbc.socialnet.dbgen.objects.ReducedUserProfile;
import ldbc.socialnet.dbgen.objects.UserExtraInfo;


public class GroupGenerator {
	static int groupId = 0;
	static int forumId;
	
	DateGenerator dateGenerator; 
	LocationDictionary locationDic;
	TagDictionary tagDic;
	Random 	randGroupInterest;
	
	public GroupGenerator(DateGenerator dateGenerator, LocationDictionary locationDic, 
			TagDictionary tagDic, int numUsers, long seed){
		this.dateGenerator = dateGenerator; 
		this.locationDic = locationDic; 
		this.tagDic = tagDic; 
		
		GroupGenerator.forumId = numUsers * 2 + 1;
		randGroupInterest = new Random(seed);
	}
	
	public void setForumId(int forumId) {
	    GroupGenerator.forumId = forumId;
	}
	
	public Group createGroup(Random random, ReducedUserProfile user){
		Group group = new Group(); 
		forumId = forumId + 2;
		groupId++;
		
		group.setGroupId(groupId);
		group.setModeratorId(user.getAccountId());
		group.setCreatedDate(dateGenerator.randomGroupCreatedDate(random,user));
		group.setForumWallId(forumId);
		group.setForumStatusId(forumId + 1);
		
		//Use the user location for group locationIdx
		group.setLocationIdx(user.getLocationId());
				
		TreeSet<Integer> tagSet = user.getSetOfTags();
		Iterator<Integer> iter = tagSet.iterator();
        int idx = randGroupInterest.nextInt(tagSet.size());
        for (int i = 0; i < idx; i++){
            iter.next();
        }
		  
		int interestIdx = iter.next().intValue();
		
		//Set tags of this group
		Integer tags[] = new Integer[1];
		tags[0] = interestIdx;
		
		//Set name of group
		group.setGroupName("Group for " + tagDic.getName(interestIdx).replace("\"","\\\"") + " in " + locationDic.getLocationName(group.getLocationIdx()));
		
		group.setTags(tags);
		
		return group; 
	}
	
	public Group createAlbum(Random random, ReducedUserProfile user, UserExtraInfo extraInfo, int numAlbum, double memberProb) {
	    Group group = createGroup(random,user);
	    group.setCreatedDate(dateGenerator.randomPhotoAlbumCreatedDate(random,user));
	    Vector<Integer> countries = locationDic.getCountries();
	    int randomCountry = random.nextInt(countries.size());
	    group.setLocationIdx(countries.get(randomCountry));
	    group.setGroupName("Album " + numAlbum + " of " + extraInfo.getFirstName() + " " + extraInfo.getLastName());
	    Friend[] friends = user.getFriendList();
	    group.initAllMemberships(user.getNumFriendsAdded());
	    for (int i = 0; i < user.getNumFriendsAdded(); i++) {
	        double randMemberProb = random.nextDouble();
	        if (randMemberProb < memberProb) {
	            GroupMemberShip memberShip = createGroupMember(random,friends[i].getFriendAcc(), 
	                    group.getCreatedDate(), friends[i]);
	            group.addMember(memberShip);
	        }
	    }
	    return group;
	}
	
	public GroupMemberShip createGroupMember(Random random, long userId, long groupCreatedDate, Friend friend){
		GroupMemberShip memberShip = new GroupMemberShip();
		memberShip.setUserId(userId);
		memberShip.setJoinDate(dateGenerator.randomGroupMemberJoinDate(random,groupCreatedDate, friend.getCreatedTime()));
		memberShip.setIP(friend.getSourceIp());
		memberShip.setBrowserIdx(friend.getBrowserIdx());
		memberShip.setAgentIdx(friend.getAgentIdx());
		memberShip.setFrequentChange(friend.isFrequentChange());
		memberShip.setHaveSmartPhone(friend.isHaveSmartPhone());
        memberShip.setLargePoster(friend.isLargePoster());
		
		return memberShip;
	}
	
	public GroupMemberShip createGroupMember(Random random, long userId, long groupCreatedDate, ReducedUserProfile user){
        GroupMemberShip memberShip = new GroupMemberShip();
        memberShip.setUserId(userId);
        memberShip.setJoinDate(dateGenerator.randomGroupMemberJoinDate(random, groupCreatedDate, user.getCreationDate()));
        memberShip.setIP(user.getIpAddress());
        memberShip.setBrowserIdx(user.getBrowserIdx());
        memberShip.setAgentIdx(user.getAgentIdx());
        memberShip.setFrequentChange(user.isFrequentChange());
        memberShip.setHaveSmartPhone(user.isHaveSmartPhone());
        memberShip.setLargePoster(user.isLargePoster());
        return memberShip;
    }
	
}
