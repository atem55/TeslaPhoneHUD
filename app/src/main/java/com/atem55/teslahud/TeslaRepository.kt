package com.atem55.teslahud

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import com.google.gson.annotations.SerializedName

data class VehicleDataResponse(
    val response: VehicleData?
)

data class VehicleData(
    @SerializedName("drive_state") val driveState: DriveState?,
    @SerializedName("charge_state") val chargeState: ChargeState?
) {
    val speed: Int? get() = driveState?.speed
    val heading: Int? get() = driveState?.heading
    val shiftState: String? get() = driveState?.shiftState
    val activeRouteDestination: String? get() = driveState?.activeRouteDestination
    val activeRouteMinutesToArrival: Double? get() = driveState?.activeRouteMinutesToArrival
    val activeRouteMilesToArrival: Double? get() = driveState?.activeRouteMilesToArrival
    val batteryLevel: Int? get() = chargeState?.batteryLevel
    val batteryRange: Double? get() = chargeState?.batteryRange
}

data class DriveState(
    val speed: Int?,
    val heading: Int?,
    @SerializedName("shift_state") val shiftState: String?,
    @SerializedName("active_route_destination") val activeRouteDestination: String?,
    @SerializedName("active_route_minutes_to_arrival") val activeRouteMinutesToArrival: Double?,
    @SerializedName("active_route_miles_to_arrival") val activeRouteMilesToArrival: Double?
)

data class ChargeState(
    @SerializedName("battery_level") val batteryLevel: Int?,
    @SerializedName("battery_range") val batteryRange: Double?
)

interface TeslaApi {
    @GET("api/1/vehicles/{id}/vehicle_data")
    suspend fun getVehicleData(
        @Header("Authorization") auth: String,
        @Path("id") vehicleId: String
    ): VehicleDataResponse
}

object TeslaRepository {
    private const val BASE_URL = "https://owner-api.teslamotors.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(TeslaApi::class.java)

    suspend fun fetchVehicleData(token: String, vehicleId: String): VehicleData? {
        return try {
            val response = api.getVehicleData("Bearer $token", vehicleId)
            response.response
        } catch (e: Exception) {
            null
        }
    }
}
