/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package listener;
/**
 * 作者： Circle
 * 创造于 2018/5/24.
 */
public interface Constants {

    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 10;
    public static final int MESSAGE_READ = 11;
    public static final int MESSAGE_WRITE = 12;
    public static final int MESSAGE_DEVICE_NAME = 13;
    public static final int MESSAGE_TOAST = 14;

    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public static final String IS_AIR_PATCH = "is_air_patch";
    public static final String PRINTER_ID = "printer_id";


}
