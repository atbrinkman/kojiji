/**
 * Copyright (C) 2015 Red Hat, Inc. (jcasey@redhat.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.red.build.koji.model.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.redhat.red.build.koji.model.util.TimestampIntValueBinder;
import com.redhat.red.build.koji.model.util.TimestampValueBinder;
import org.commonjava.maven.atlas.ident.ref.ProjectVersionRef;
import org.commonjava.rwx.binding.anno.Converter;
import org.commonjava.rwx.binding.anno.DataKey;
import org.commonjava.rwx.binding.anno.KeyRefs;
import org.commonjava.rwx.binding.anno.StructPart;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.redhat.red.build.koji.model.json.KojiJsonConstants.END_TIME;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.EXTRA_INFO;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.NAME;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.RELEASE;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.SOURCE;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.START_TIME;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.TYPE;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.VERSION;
import static com.redhat.red.build.koji.model.json.util.Verifications.checkNull;
import static com.redhat.red.build.koji.model.json.util.Verifications.checkString;
import static com.redhat.red.build.koji.model.util.KojiFormats.toKojiName;
import static com.redhat.red.build.koji.model.util.KojiFormats.toKojiVersion;

/**
 * Created by jdcasey on 2/10/16.
 */
@StructPart
public class BuildDescription
{
    @JsonProperty( NAME )
    @DataKey( NAME )
    private String name;

    @JsonProperty( VERSION )
    @DataKey( VERSION )
    private String version;

    @JsonProperty( RELEASE )
    @DataKey( RELEASE )
    private String release = "1";

    @JsonProperty( START_TIME )
    @DataKey( START_TIME )
    @Converter( TimestampIntValueBinder.class )
    private Date startTime;

    @JsonProperty( END_TIME )
    @DataKey( END_TIME )
    @Converter( TimestampIntValueBinder.class )
    private Date endTime;

    @JsonProperty( TYPE )
    @DataKey( TYPE )
    private String buildType;

    @JsonProperty( SOURCE )
    @DataKey( SOURCE )
    private BuildSource source;

    @JsonProperty( EXTRA_INFO )
    @DataKey( EXTRA_INFO )
    private BuildExtraInfo extraInfo;

    private BuildDescription(){}

    @JsonCreator
    @KeyRefs( { NAME, VERSION, RELEASE, START_TIME, END_TIME, TYPE, SOURCE } )
    public BuildDescription( @JsonProperty( NAME ) String name, @JsonProperty( VERSION ) String version,
                             @JsonProperty( RELEASE ) String release, @JsonProperty( START_TIME ) Date startTime,
                             @JsonProperty( END_TIME ) Date endTime, @JsonProperty( TYPE ) String buildType,
                             @JsonProperty( SOURCE ) BuildSource source )
    {
        this.name = name;
        this.version = version;
        this.release = release;
        this.startTime = startTime;
        this.endTime = endTime;
        this.buildType = buildType;
        this.source = source;
    }

    public String getName()
    {
        return name;
    }

    public String getVersion()
    {
        return version;
    }

    public String getRelease()
    {
        return release;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public String getBuildType()
    {
        return buildType;
    }

    public BuildSource getSource()
    {
        return source;
    }

    public BuildExtraInfo getExtraInfo()
    {
        return extraInfo;
    }

    public void setExtraInfo( BuildExtraInfo extraInfo )
    {
        this.extraInfo = extraInfo;
    }

    public static final class Builder
            implements SectionBuilder<BuildDescription>, VerifiableBuilder<BuildDescription>
    {
        private KojiImport.Builder parent;

        private BuildDescription target = new BuildDescription();

        public Builder( ProjectVersionRef gav )
        {
            target.name = toKojiName( gav );
            target.version = toKojiVersion( gav.getVersionString() );
            target.release = "1";
            withMavenInfoAndType( gav );
        }

        public Builder( ProjectVersionRef gav, KojiImport.Builder parent )
        {
            this.parent = parent;
            target.name = toKojiName( gav );
            target.version = toKojiVersion( gav.getVersionString() );
            target.release = "1";
            withMavenInfoAndType( gav );
        }

        public Builder( String name, String version )
        {
            target.name = name;
            target.version = version;
        }

        public Builder( String name, String version, String release )
        {
            target.name = name;
            target.version = version;
            target.release = release;
        }

        public Builder( String name, String version, String release, KojiImport.Builder parent )
        {
            target.name = name;
            target.version = version;
            target.release = release;
            this.parent = parent;
        }

        public KojiImport.Builder parent()
        {
            return parent;
        }

        public Builder withStartTime( Date start )
        {
            target.startTime = start;
            return this;
        }

        public Builder withEndTime( Date end )
        {
            target.endTime = end;
            return this;
        }

        public Builder withBuildType( StandardBuildType type )
        {
            target.buildType = type.name();
            return this;
        }

        public Builder withBuildType( String type )
        {
            target.buildType = type;
            return this;
        }

        public Builder withBuildSource( String url )
        {
            target.source = new BuildSource( url );
            return this;
        }

        public Builder withBuildSource( String url, String revision )
        {
            target.source = new BuildSource( url );
            target.source.setRevision( revision );

            return this;
        }

        public Builder withBuildSource( BuildSource source )
        {
            target.source = source;
            return this;
        }

        public Builder withMavenInfoAndType( ProjectVersionRef gav )
        {
            if ( target.extraInfo == null )
            {
                target.extraInfo = new BuildExtraInfo();
            }

            target.buildType = StandardOutputType.maven.name();
            target.extraInfo.setMavenExtraInfo(
                    new MavenExtraInfo( gav.getGroupId(), gav.getArtifactId(), gav.getVersionString() ) );

            return this;
        }

        public Builder withExternalBuildId( String buildId )
        {
            if ( target.extraInfo == null )
            {
                target.extraInfo = new BuildExtraInfo();
            }

            target.extraInfo.setExternalBuildId( buildId );

            return this;
        }

        @Override
        public BuildDescription build()
                throws VerificationException
        {
            Set<String> missing = new HashSet<>();
            findMissingProperties( "%s", missing );
            if ( !missing.isEmpty() )
            {
                throw new VerificationException( missing );
            }

            return unsafeBuild();
        }

        @Override
        public BuildDescription unsafeBuild()
        {
            return target;
        }

        @Override
        public void findMissingProperties( String prefix, Set<String> missingProperties )
        {
            checkString( target.name, missingProperties, prefix, NAME );
            checkString( target.version, missingProperties, prefix, VERSION );
            checkString( target.release, missingProperties, prefix, RELEASE );
            checkString( target.buildType, missingProperties, prefix, TYPE );
            checkNull( target.startTime, missingProperties, prefix, START_TIME );
            checkNull( target.endTime, missingProperties, prefix, END_TIME );
//            checkNull( target.source, missingProperties, prefix, SOURCE );
        }
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof BuildDescription ) )
        {
            return false;
        }

        BuildDescription that = (BuildDescription) o;

        if ( getName() != null ? !getName().equals( that.getName() ) : that.getName() != null )
        {
            return false;
        }
        if ( getVersion() != null ? !getVersion().equals( that.getVersion() ) : that.getVersion() != null )
        {
            return false;
        }
        if ( getRelease() != null ? !getRelease().equals( that.getRelease() ) : that.getRelease() != null )
        {
            return false;
        }
        if ( getStartTime() != null ? !getStartTime().equals( that.getStartTime() ) : that.getStartTime() != null )
        {
            return false;
        }
        if ( getEndTime() != null ? !getEndTime().equals( that.getEndTime() ) : that.getEndTime() != null )
        {
            return false;
        }
        if ( getBuildType() != null ? !getBuildType().equals( that.getBuildType() ) : that.getBuildType() != null )
        {
            return false;
        }
        if ( source != null ? !source.equals( that.source ) : that.source != null )
        {
            return false;
        }
        return !( getExtraInfo() != null ? !getExtraInfo().equals( that.getExtraInfo() ) : that.getExtraInfo() != null );

    }

    @Override
    public int hashCode()
    {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + ( getVersion() != null ? getVersion().hashCode() : 0 );
        result = 31 * result + ( getRelease() != null ? getRelease().hashCode() : 0 );
        result = 31 * result + ( getStartTime() != null ? getStartTime().hashCode() : 0 );
        result = 31 * result + ( getEndTime() != null ? getEndTime().hashCode() : 0 );
        result = 31 * result + ( getBuildType() != null ? getBuildType().hashCode() : 0 );
        result = 31 * result + ( source != null ? source.hashCode() : 0 );
        result = 31 * result + ( getExtraInfo() != null ? getExtraInfo().hashCode() : 0 );
        return result;
    }

    @Override
    public String toString()
    {
        return "BuildDescription{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", release='" + release + '\'' +
                ", startTime=" + startTime == null ? "null" : startTime.getTime() +
                ", endTime=" + endTime == null ? "null" : endTime.getTime() +
                ", buildType='" + buildType + '\'' +
                ", source=" + source +
                ", extraInfo=" + extraInfo +
                '}';
    }
}