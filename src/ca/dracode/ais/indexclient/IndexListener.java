/*******************************************************************************
 * Copyright 2014 Benjamin Winger.
 *
 * This file is part of AIS Client Library.
 *
 * AIS Client Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AIS Client Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with AIS Client Library.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/


package ca.dracode.ais.indexclient;

public interface IndexListener {
    /**
     * Called when an index has been created using the IndexClient.buildIndex() function
     * @param path Path for the file added to the index
     */
    public void indexCreated(String path);

    /**
     * Called when a file has been loaded or has failed to be loaded by the Service
     * @param path Path for the file
     * @param loaded 0 if the file exists in the index and was not already loaded;
     *	 			1 if the file was already loaded;
     *			2 if the file was not loaded and does not exist in the index;
     *			-1 if there was an error
     */
    public void indexLoaded(String path, int loaded);

    /**
     * Called when a file was unloaded by the Service
     * @param path Path for the file
     * @param unloaded true if the file was previously loaded, false otherwise
     */
    public void indexUnloaded(String path, boolean unloaded);

    /**
     * Called if there was an unexpected error while searching
     * @param text The term that was originally searched for
     * @param index File that was being searched
     */
    public void errorWhileSearching(String text, String index);

    /**
     * Called when the IndexClient has connected to the ClientService; Files can now be loaded
     * and searches can now be made
     */
    public void connectedToService();

    /**
     * Called when the IndexClient has disconnected from the ClientService,
     * the ClientService should now be disposed of
     */
    public void disconnectedFromService();
}
