/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/benjamin/workspace/IndexClient/src/com/bmw/android/indexservice/BSearchService.aidl
 */
package com.bmw.android.indexservice;
public interface BSearchService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.bmw.android.indexservice.BSearchService
{
private static final java.lang.String DESCRIPTOR = "com.bmw.android.indexservice.BSearchService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.bmw.android.indexservice.BSearchService interface,
 * generating a proxy if needed.
 */
public static com.bmw.android.indexservice.BSearchService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.bmw.android.indexservice.BSearchService))) {
return ((com.bmw.android.indexservice.BSearchService)iin);
}
return new com.bmw.android.indexservice.BSearchService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_find:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
java.lang.String _arg2;
_arg2 = data.readString();
int _arg3;
_arg3 = data.readInt();
int _arg4;
_arg4 = data.readInt();
java.util.List<com.bmw.android.indexdata.Result> _result = this.find(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_buildIndex:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.util.List<java.lang.String> _arg1;
_arg1 = data.createStringArrayList();
double _arg2;
_arg2 = data.readDouble();
int _arg3;
_arg3 = data.readInt();
int _result = this.buildIndex(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_load:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.load(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_unload:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.unload(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.bmw.android.indexservice.BSearchService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
// VERSION 1.0
/**
	 * Used to search file contents
	 * @param 	doc - the name of the document that should be searched. This allows metadata 
	 *				for multiple files to be in the search service's memory at once.
	 * 			type - allows the client to specify what type of results it wants to receive
	 * 			text - the search query
	 * 			numHits - the number of results the client wants to receive
	 * 			page - the starting page for results (if results end up on a page
	 *				 before this page they are pushed to the end of the returned list)
	 * @return a list containing the terms found that matched the query and what page of the document they appear on.
	 */
@Override public java.util.List<com.bmw.android.indexdata.Result> find(java.lang.String doc, int type, java.lang.String text, int numHits, int page) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<com.bmw.android.indexdata.Result> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(doc);
_data.writeInt(type);
_data.writeString(text);
_data.writeInt(numHits);
_data.writeInt(page);
mRemote.transact(Stub.TRANSACTION_find, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(com.bmw.android.indexdata.Result.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * used to send file contents to the indexing service. Because of the limitations of 
	 * the service communication system the information may have to be sent in chunks as
	 * there can only be a maximum of about 1MB in the buffer at a time (which is shared 
	 * among all applications). The client class sends data in chunks that do not exceed 256KB,
	 * @param 	filePath - the location of the file to be built; used by the indexer to identify the file
	 *			text - the text to be added to the index
	 *			page - the page upon which the chunk of the file that is being transferred starts. 
	 *					It is a double to allow the transfer of parts of a single page if the page is too large
	 *			maxPage - the total number of pages in the entire file
	 * @return 	0 if index was built successfully; 
	 *			1 if index failed to build; 
	 * 			2 if the file lock was in place due to another build operation being in progress;
	 *			-1 on error
	 */
@Override public int buildIndex(java.lang.String filePath, java.util.List<java.lang.String> text, double page, int maxPage) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(filePath);
_data.writeStringList(text);
_data.writeDouble(page);
_data.writeInt(maxPage);
mRemote.transact(Stub.TRANSACTION_buildIndex, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Tells the indexer to load a file's metadata into memory for use in searches.
	 * @param filePath - the location of the file to prepare; is also the identifier for the file's data in the index
	 * @return 0 if the file exists in the index and was not already loaded; 1 if the file was already loaded; 
	 *			2 if the file was not loaded and does not exist in the index; -1 if there was an error
	 */
@Override public int load(java.lang.String filePath) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(filePath);
mRemote.transact(Stub.TRANSACTION_load, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Tells the indexer to unload a file's metadata from memory as it will not be used in future searches.
	 * @param filePath - the location of the file; used to identify which file should be unloaded
	 * @return true if the file exists in the index; false otherwise
	 */
@Override public boolean unload(java.lang.String filePath) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(filePath);
mRemote.transact(Stub.TRANSACTION_unload, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_find = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_buildIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_load = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_unload = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
// VERSION 1.0
/**
	 * Used to search file contents
	 * @param 	doc - the name of the document that should be searched. This allows metadata 
	 *				for multiple files to be in the search service's memory at once.
	 * 			type - allows the client to specify what type of results it wants to receive
	 * 			text - the search query
	 * 			numHits - the number of results the client wants to receive
	 * 			page - the starting page for results (if results end up on a page
	 *				 before this page they are pushed to the end of the returned list)
	 * @return a list containing the terms found that matched the query and what page of the document they appear on.
	 */
public java.util.List<com.bmw.android.indexdata.Result> find(java.lang.String doc, int type, java.lang.String text, int numHits, int page) throws android.os.RemoteException;
/**
	 * used to send file contents to the indexing service. Because of the limitations of 
	 * the service communication system the information may have to be sent in chunks as
	 * there can only be a maximum of about 1MB in the buffer at a time (which is shared 
	 * among all applications). The client class sends data in chunks that do not exceed 256KB,
	 * @param 	filePath - the location of the file to be built; used by the indexer to identify the file
	 *			text - the text to be added to the index
	 *			page - the page upon which the chunk of the file that is being transferred starts. 
	 *					It is a double to allow the transfer of parts of a single page if the page is too large
	 *			maxPage - the total number of pages in the entire file
	 * @return 	0 if index was built successfully; 
	 *			1 if index failed to build; 
	 * 			2 if the file lock was in place due to another build operation being in progress;
	 *			-1 on error
	 */
public int buildIndex(java.lang.String filePath, java.util.List<java.lang.String> text, double page, int maxPage) throws android.os.RemoteException;
/**
	 * Tells the indexer to load a file's metadata into memory for use in searches.
	 * @param filePath - the location of the file to prepare; is also the identifier for the file's data in the index
	 * @return 0 if the file exists in the index and was not already loaded; 1 if the file was already loaded; 
	 *			2 if the file was not loaded and does not exist in the index; -1 if there was an error
	 */
public int load(java.lang.String filePath) throws android.os.RemoteException;
/**
	 * Tells the indexer to unload a file's metadata from memory as it will not be used in future searches.
	 * @param filePath - the location of the file; used to identify which file should be unloaded
	 * @return true if the file exists in the index; false otherwise
	 */
public boolean unload(java.lang.String filePath) throws android.os.RemoteException;
}
