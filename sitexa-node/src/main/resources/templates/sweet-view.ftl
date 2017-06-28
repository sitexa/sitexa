<#import "template.ftl" as layout />

<@layout.mainLayout title="View sweet">

<#-- @ftlvariable name="media" type="com.sitexa.ktor.model.Media" -->
<section class="post">
    <header class="post-header">
        <p class="post-meta">
        ${sweet.userId}
            <span>${sweet.date.toDate()?string("yyyy.MM.dd HH:mm:ss")}</span>
        </p>
    </header>
    <div class="post-description">${sweet.text}</div>
    <#list medias as media>
    <#if media?? && media.fileType??>
        <#if media.fileType="image">
            <div class="post-images">
                <img class="post-images" src="/media/${media.fileName}/${media.fileType}"/>
            </div>
        <#elseif  media.fileType="video">
            <video width="400" controls>
                <source src="/media/${media.fileName}/${media.fileType}" type="video/mp4">
                Your browser does not support HTML5 video.
            </video>
        <#elseif media.fileType="audio">
            <audio controls>
                <source src="/media/${media.fileName}/${media.fileType}" type="audio/ogg">
                Your browser does not support the audio element.
            </audio>
        </#if>
    </#if>
    </#list>
</section>
<section class="reply">
    <#list replies as reply>
        <header class="reply-header">
            <div class="reply-meta">
            ${reply.userId}
                <span>${reply.date.toDate()?string("yyyy.MM.dd HH:mm:ss")}</span></div>
        </header>
        <div class="reply-description">
        ${reply.text}
        </div>
    </#list>
</section>

    <#if user??>
        <#if user.userId==sweet.userId>
        <p>
            <a href="javascript:void(0)" onclick="document.getElementById('editForm').submit()">Edit </a>
            <a href="javascript:void(0)" onclick="document.getElementById('deleteForm').submit()">Delete </a>
        </p>

        <form id="deleteForm" method="post" action="/sweet-del" enctype="application/x-www-form-urlencoded">
            <input type="hidden" name="id" value="${sweet.id}">
            <input type="hidden" name="date" value="${date?c}">
            <input type="hidden" name="code" value="${code}">
        </form>
        <form id="editForm" method="get" action="/sweet-upd" enctype="application/x-www-form-urlencoded">
            <input type="hidden" name="id" value="${sweet.id}">
            <input type="hidden" name="date" value="${date?c}">
            <input type="hidden" name="code" value="${code}">
        </form>
        </#if>
    <p>
        <a href="javascript:void(0)" onclick="document.getElementById('replyForm').submit()">Reply </a>
    </p>
    <form id="replyForm" method="get" action="/sweet-reply" enctype="application/x-www-form-urlencoded">
        <input type="hidden" name="replyTo" value="${sweet.id}">
        <input type="hidden" name="date" value="${date?c}">
        <input type="hidden" name="code" value="${code}">
    </form>
    </#if>

</@layout.mainLayout>