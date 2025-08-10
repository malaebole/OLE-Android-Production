package ae.oleapp.api;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIInterface {
    @FormUrlEncoded
    @POST("get_cities")
    Call<ResponseBody> getCountries(@Field("language_id") String langId); //pass

    @Multipart
    @POST("v2/complete_signup") //pass
    Call<ResponseBody> pregister(@Part MultipartBody.Part part,
                                 @Part("language_id") RequestBody langId,
                                 @Part("user_id") RequestBody userId,
                                 @Part("name") RequestBody name,
                                 @Part("first_name") RequestBody fName,
                                 @Part("last_name") RequestBody lName,
                                 @Part("nick_name") RequestBody nickName,
                                 @Part("country_id") RequestBody countryId,
                                 @Part("city_id") RequestBody cityId,
                                 @Part("dob") RequestBody dob,
                                 @Part("email") RequestBody email,
                                 @Part("gender") RequestBody gender,
                                 @Part("user_type") RequestBody type,
                                 @Part("device_type") RequestBody deviceType,
                                 @Part("user_bib") RequestBody userBib);




    @FormUrlEncoded
    @POST("v2/complete_signup") //pass
    Call<ResponseBody> register(@Field("language_id") String langId,
                                @Field("user_id") String userId,
                                @Field("name") String name,
                                @Field("email") String email,
                                @Field("user_type") String type,
                                @Field("device_type") String deviceType);


    @FormUrlEncoded
    @POST("v2/resend_code")  //pass
    Call<ResponseBody> resendCodeV2(@Field("language_id") String langId,
                                    @Field("id") String id);

    @FormUrlEncoded
    @POST("v2/verify_otp") //pass
    Call<ResponseBody> verifyCodeV2(@Field("language_id") String langId,
                                    @Field("user_id") String id,
                                    @Field("otp_code") String code);

    @FormUrlEncoded
    @POST("device_tokens") //pass
    Call<ResponseBody> sendFcmToken(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("device_id") String deviceId,
                                  @Field("device_type") String type,
                                  @Field("device_token") String token,
                                  @Field("app_version") String appVersion);

    @FormUrlEncoded
    @POST("set_language")
    Call<ResponseBody> sendAppLang(@Field("user_id") String userId,
                                    @Field("language_id") String langId);

    @FormUrlEncoded
    @POST("login") //pass
    Call<ResponseBody> login(@Field("language_id") String langId,
                             @Field("username") String username,
                             @Field("password") String pass,
                             @Field("is_employee") String emp);

    @FormUrlEncoded
    @POST("v2/login") //pass
    Call<ResponseBody> withPasswordlogin(@Field("language_id") String langId,
                             @Field("phone") String username,
                             @Field("password") String pass,
                             @Field("fcm_token") String fcmToken,
                             @Field("device_type") String deviceType);

    @FormUrlEncoded
    @POST("v2/send_otp") //pass
    Call<ResponseBody> loginWithPhone(@Field("language_id") String langId,
                                      @Field("phone") String phone,
                                      @Field("fcm_token") String fcmToken,
                                      @Field("device_type") String deviceType);
                                      //@Field("user_password") String password)

    @FormUrlEncoded
    @POST("get_carousel") //pass
    Call<ResponseBody> getCarousel(@Field("language_id") String langId);


    @FormUrlEncoded
    @POST("forget_password") //pass
    Call<ResponseBody> resetPass(@Field("language_id") String langId,
                                 @Field("username") String username);


    @FormUrlEncoded
    @POST("reset_password") //pass
    Call<ResponseBody> recoverPass(@Field("language_id") String langId,
                                   @Field("otp_code") String code,
                                   @Field("password") String password);

    @FormUrlEncoded
    @POST("club_list") //pass
    Call<ResponseBody> getClubList(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("latitude") double lat,
                                   @Field("longitude") double longitude,
                                   @Field("limits") int pageNo,
                                   @Field("offers") String offers,
                                   @Field("name") String name,
                                   @Field("date") String date,
                                   @Field("time_start") String timeStart,
                                   @Field("time_end") String timeEnd,
                                   @Field("location") String cityId,
                                   @Field("field_grass") String grassType,
                                   @Field("field_type") String fieldType,
                                   @Field("field_size") String fieldSize,
                                   @Field("nearby") String nearby,
                                   @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("add_remove_favorites") //pass
    Call<ResponseBody> addRemoveFav(@Field("language_id") String langId,
                                   @Field("user_id") String useId,
                                   @Field("favorite_id") String favoriteId,
                                   @Field("status") String status,
                                   @Field("type") String type,
                                    @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("get_field_data") //pass
    Call<ResponseBody> getFieldData(@Field("language_id") String langId);

    @FormUrlEncoded
    @POST("add_reviews") //pass
    Call<ResponseBody> rateClub(@Field("language_id") String langId,
                                @Field("user_id") String userId,
                                @Field("club_id") String clubId,
                                @Field("stars") float rating);

    @FormUrlEncoded
    @POST("get_all_fields") //pass
    Call<ResponseBody> getAllFields(@Field("language_id") String langId,
                                    @Field("user_id") String userId,
                                    @Field("club_id") String clubId);
    @FormUrlEncoded
    @POST("add_user_to_waiting_list") //pass
    Call<ResponseBody> addUserToWaitingList(@Field("booking_id") String bookingId,
                                            @Field("club_id") String clubId,
                                            @Field("user_name") String userName,
                                            @Field("user_phone") String userPhone);

    @FormUrlEncoded
    @POST("remove_waiting_user") //pass
    Call<ResponseBody> removeWaitingUser(@Field("remove_id") String removeId);

    @FormUrlEncoded
    @POST("find_phone_details") //pass
    Call<ResponseBody> findPhoneDetails(@Field("user_phone") String bookingId,
                                        @Field("club_id") String clubId);


    @FormUrlEncoded
    @POST("get_all_fields") //pass
    Call<ResponseBody> getAllFields(@Field("language_id") String langId,
                                    @Field("user_id") String userId,
                                    @Field("club_id") String clubId,
                                    @Field("duration") String duration,
                                    @Field("date") String date,
                                    @Field("is_needed") String isNeeded,
                                    @Field("is_fast") String isFast);

    @FormUrlEncoded
    @POST("change_password") //pass
    Call<ResponseBody> updatePass(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("old") String oldPass,
                                  @Field("new") String newPass);

    @FormUrlEncoded
    @POST("get_one_field") //pass
    Call<ResponseBody> getOneField(@Field("language_id") String langId,
                                    @Field("user_id") String userId,
                                    @Field("field_id") String fieldId);

    @FormUrlEncoded
    @POST("get_slots") //pass
    Call<ResponseBody> getSlots(@Field("language_id") String langId,
                                @Field("user_id") String userId,
                                @Field("field_id") String fieldId,
                                @Field("club_id") String clubId,
                                @Field("duration") String duration,
                                @Field("date") String date,
                                @Field("is_needed") String isDateNeeded,
                                @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("notify_availability") //pass
    Call<ResponseBody> notifyAvailability(@Field("language_id") String langId,
                                          @Field("field_id") String fieldId,
                                          @Field("club_id") String clubId,
                                          @Field("time_start") String timeStart,
                                          @Field("time_end") String timeEnd,
                                          @Field("phone") String phone,
                                          @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("coupon_discount") //pass
    Call<ResponseBody> promoDiscount(@Field("language_id") String langId,
                                     @Field("field_id") String fieldId,
                                     @Field("club_id") String clubId,
                                     @Field("booking_price") String bookingPrice,
                                     @Field("booking_date") String bookingDate,
                                     @Field("coupen_code") String code,
                                     @Field("duration") String duration,
                                     @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("add_booking") //pass
    Call<ResponseBody> addBooking(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("field_id") String fieldId,
                                      @Field("club_id") String clubId,
                                      @Field("duration") String duration,
                                      @Field("date") String date,
                                      @Field("price") double price,
                                      @Field("discount") String discount,
                                      @Field("time_start") String timeStart,
                                      @Field("time_end") String timeEnd,
                                      @Field("shift") String shift,
                                      @Field("payment_method") String paymentMethod,
                                      @Field("user_name") String userName,
                                      @Field("user_phone") String phone,
                                      @Field("facilities") String facilities,
                                      @Field("facilities_price") double facilitiesPrice,
                                      @Field("offer_price") double offerPrice,
                                      @Field("tax_amount") String taxAmount,
                                      @Field("promo_discount") int promoDiscount,
                                      @Field("promo_id") String promoId,
                                      @Field("days") String days,
                                      @Field("from_date") String fromDate,
                                      @Field("to_date") String toDate,
                                      @Field("user_ip") String ip,
                                      @Field("booking_field_type") String bookingFieldType,
                                      @Field("padel_players") String padelPlayers,
                                      @Field("padel_players_for_payment") String padelPlayersPayment,
                                      @Field("lady_slot") String ladySlot,
                                      @Field("is_waiting_user") String isWaitingUser,
                                      @Field("registered_user_id") String registeredUserId);

    @FormUrlEncoded
    @POST("player_bookings") //pass
    Call<ResponseBody> getPlayerBookings(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("from") String from,
                                     @Field("to") String to,
                                     @Field("is_needed") String isNeeded,
                                     @Field("date") String date,
                                     @Field("all_dates") String allDates,
                                     @Field("club_id") String clubId,
                                     @Field("field_id") String fieldId,
                                         @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("booking_details") //pass
    Call<ResponseBody> getPlayerBookingDetail(@Field("language_id") String langId,
                                              @Field("booking_id") String bookingId,
                                              @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("booking_confirm_cancel") //pass
    Call<ResponseBody> cancelConfirmBooking(@Field("language_id") String langId,
                                            @Field("booking_id") String bookingId,
                                            @Field("flag") String flag,
                                            @Field("note") String note,
                                            @Field("discount") String discount,
                                            @Field("bill_number") String invoiceNo,
                                            @Field("extra_minutes") String extraTime,
                                            @Field("extra_price") String price,
                                            @Field("user_id") String userId);

    @Multipart
    @POST("booking_confirm_cancel") //pass
    Call<ResponseBody> cancelConfirmBooking(@Part MultipartBody.Part file,
                                            @Part("language_id") RequestBody langId,
                                            @Part("booking_id") RequestBody bookingId,
                                            @Part("flag") RequestBody flag,
                                            @Part("note") RequestBody note,
                                            @Part("discount") RequestBody discount,
                                            @Part("bill_number") RequestBody billNumber,
                                            @Part("extra_minutes") RequestBody extraMinutes,
                                            @Part("extra_price") RequestBody extraPrice,
                                            @Part("card_amount") RequestBody cardAmount,
                                            @Part("balance") RequestBody balance,
                                            @Part("user_id") RequestBody userId);

    @FormUrlEncoded
    @POST("booking_facilities") //pass
    Call<ResponseBody> updateFacility(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("booking_id") String bookingId,
                                      @Field("facilities") String fac,
                                      @Field("facilities_price") double price);

    @FormUrlEncoded
    @POST("favorites_list") //pass
    Call<ResponseBody> getFavList(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("user_rankings") //pass
    Call<ResponseBody> getRanking(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("type") String type,
                                  @Field("limits") String limits,
                                  @Field("from") String from,
                                  @Field("to") String to,
                                  @Field("min_age") String minAge,
                                  @Field("max_age") String maxAge,
                                  @Field("club_id") String clubId,
                                  @Field("level_id") String levelId,
                                  @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("single_player") //pass
    Call<ResponseBody> singlePlayer(@Field("language_id") String langId,
                                    @Field("user_id") String userId,
                                    @Field("player_id") String playerId,
                                    @Field("booking_id") String bookingId,
                                    @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("profile_match_histroy") //pass
    Call<ResponseBody> profileMatchHistroy(@Field("language_id") String langId,
                                           @Field("user_id") String userId,
                                           @Field("player_id") String playerId,
                                           @Field("from") String from,
                                           @Field("to") String to,
                                           @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("delete_user_photo") //pass
    Call<ResponseBody> deletePhoto(@Field("language_id") String langId,
                                    @Field("user_id") String userId);

    @Multipart
    @POST("update_user_photo") //pass
    Call<ResponseBody> updateProfilePhoto(@Part MultipartBody.Part file,
                                          @Part("language_id") RequestBody langId,
                                          @Part("user_id") RequestBody userId);


    @FormUrlEncoded
    @POST("get_one_user") //pass
    Call<ResponseBody> getOneUser(@Field("language_id") String langId,
                                  @Field("id") String userId);

    @FormUrlEncoded
    @POST("update_user") //pass
    Call<ResponseBody> updateUser(@Field("language_id") String langId,
                                  @FieldMap Map<String, String> attrParam);

    @FormUrlEncoded
    @POST("unread_notification_count") //pass
    Call<ResponseBody> unreadNotifCount(@Field("language_id") String langId,
                                        @Field("user_id") String userId,
                                        @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("notification_list")    //pass
    Call<ResponseBody> notificationList(@Field("language_id") String langId,
                                       @Field("user_id") String userId,
                                        @Field("club_id") String clubId,
                                        @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("delete_notification")     //pass
    Call<ResponseBody> deleteNotification(@Field("language_id") String langId,
                                        @Field("id") String notId);

    @FormUrlEncoded
    @POST("clear_notifications") //pass
    Call<ResponseBody> deleteAllNotification(@Field("language_id") String langId,
                                             @Field("user_id") String userId,
                                             @Field("club_id") String clubId,
                                             @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("read_all_notification") //pass
    Call<ResponseBody> readAllNotification(@Field("language_id") String langId,
                                           @Field("user_id") String userId,
                                           @Field("club_id") String clubId,
                                           @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("read_notification") //pass
    Call<ResponseBody> readNotification(@Field("language_id") String langId,
                                           @Field("id") String notId);

    @FormUrlEncoded
    @POST("contact_details") //pass
    Call<ResponseBody> getContactDetails(@Field("language_id") String langId);

    @FormUrlEncoded
    @POST("add_contact_us") //pass
    Call<ResponseBody> addContactUs(@Field("language_id") String langId,
                                    @Field("name") String name,
                                    @Field("email") String email,
                                    @Field("phone") String phone,
                                    @Field("message") String message);

    @FormUrlEncoded
    @POST("get_earnings") //pass
    Call<ResponseBody> getEarnings(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("from") String from,
                                   @Field("to") String to,
                                   @Field("type") String type,
                                   @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("create_match") //pass
    Call<ResponseBody> createMatch(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("booking_id") String bookingId,
                                   @Field("match_type") String matchType,
                                   @Field("required_player") String reqPlayers,
                                   @Field("total_players") String totalPlayers,
                                   @Field("min_age") String minAge,
                                   @Field("max_age") String maxAge,
                                   @Field("player_ids") String playerIds,
                                   @Field("order_ref") String orderRef,
                                   @Field("card_paid") String cardPaid,
                                   @Field("wallet_paid") String walletPaid,
                                   @Field("payment_method") String paymentMethod,
                                   @Field("only_favorites") String fav,
                                   @Field("user_ip") String ip,
                                   @Field("group_id") String groupId);

    @FormUrlEncoded
    @POST("players_list") //pass
    Call<ResponseBody> getPlayerList(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("latitude") double latitude,
                                   @Field("longitude") double longitude,
                                   @Field("limits") int pageNo,
                                   @Field("name") String name,
                                   @Field("location") String location,
                                   @Field("age") String age,
                                   @Field("matches") String matches,
                                   @Field("points") String points,
                                   @Field("top_100") String top100,
                                   @Field("this_month") String thisMonth,
                                   @Field("overall") String overall,
                                     @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("delete_invited_player") //pass
    Call<ResponseBody> deleteInvitedPlayer(@Field("language_id") String langId,
                                   @Field("player_id") String playerId,
                                   @Field("booking_id") String booking,
                                   @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("cancel_accepted_match") //pass
    Call<ResponseBody> cancelAcceptedMatch(@Field("language_id") String langId,
                                           @Field("player_id") String playerId,
                                           @Field("booking_id") String bookingId,
                                           @Field("booking_type") String bookingType,
                                           @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("invite_more_player") //pass
    Call<ResponseBody> inviteMorePlayers(@Field("language_id") String langId,
                                           @Field("player_ids") String playerIds,
                                           @Field("booking_id") String bookingId,
                                           @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("get_requested_players") //pass
    Call<ResponseBody> getRequest(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("booking_id") String bookingId,
                                  @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("match_accept_reject") //pass
    Call<ResponseBody> acceptRejectChallenge(@Field("language_id") String langId,
                                            @Field("user_id") String userId,
                                            @Field("booking_id") String bookingId,
                                             @Field("player_id") String playerId,
                                             @Field("booking_type") String bookingType,
                                             @Field("request_status") String reqStatus,
                                             @Field("flag") String flag);

    @FormUrlEncoded
    @POST("match_accept_reject") //pass
    Call<ResponseBody> acceptRejectChallenge(@Field("language_id") String langId,
                                             @Field("user_id") String userId,
                                             @Field("booking_id") String bookingId,
                                             @Field("player_id") String playerId,
                                             @Field("booking_type") String bookingType,
                                             @Field("request_status") String reqStatus,
                                             @Field("flag") String flag,
                                             @Field("order_ref") String orderRef,
                                             @Field("card_paid") String cardPaid,
                                             @Field("wallet_paid") String walletPaid,
                                             @Field("payment_method") String paymentMethod,
                                             @Field("user_ip") String ip,
                                             @Field("joining_fee") String joinFee);

    @FormUrlEncoded
    @POST("match_private_to_public") //pass
    Call<ResponseBody> changePrivateMatchToPublic(@Field("language_id") String langId,
                                                  @Field("user_id") String userId,
                                                  @Field("booking_id") String bookingId,
                                                  @Field("min_age") String minAge,
                                                  @Field("max_age") String maxAge);

    @FormUrlEncoded
    @POST("matches_with_other_players") //pass
    Call<ResponseBody> matchHistory(@Field("language_id") String langId,
                                    @Field("user_id") String playerId);

    @FormUrlEncoded
    @POST("player_to_player_match_history") //pass
    Call<ResponseBody> matchHistoryDetail(@Field("language_id") String langId,
                                    @Field("user_id") String playerId1,
                                    @Field("player_id") String playerId2);

    @FormUrlEncoded
    @POST("game_rating_summary") //pass
    Call<ResponseBody> gameHistoryOle(@Field("language_id") String langId,
                                   @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("player_vs_player") //pass
    Call<ResponseBody> getMatchesList(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("game_rating_history") //pass
    Call<ResponseBody> getReviews(@Field("language_id") String langId,
                                   @Field("user_id") String playerId);

    @FormUrlEncoded
    @POST("join_challenge") //pass
    Call<ResponseBody> joinChallenge(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("booking_id") String bookingId,
                                     @Field("match_type") String matchType,
                                     @Field("order_ref") String orderRef,
                                     @Field("card_paid") String cardPaid,
                                     @Field("wallet_paid") String walletPaid,
                                     @Field("payment_method") String paymentMethod,
                                     @Field("user_ip") String ip,
                                     @Field("joining_fee") String joinFee);

    @FormUrlEncoded
    @POST("join_game") //pass
    Call<ResponseBody> joinGame(@Field("language_id") String langId,
                                @Field("user_id") String userId,
                                @Field("booking_id") String bookingId,
                                @Field("player_position") String position,
                                @Field("joining_fee") String fee,
                                @Field("order_ref") String orderRef,
                                @Field("card_paid") String cardPaid,
                                @Field("wallet_paid") String walletPaid,
                                @Field("payment_method") String paymentMethod,
                                @Field("user_ip") String ip);

    @FormUrlEncoded
    @POST("all_matches") //pass
    Call<ResponseBody> allMatches(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("latitude") double latitude,
                                  @Field("longitude") double longitude,
                                  @Field("search_text") String name,
                                  @Field("from_date") String from,
                                  @Field("to_date") String to,
                                  @Field("club_id") String clubId,
                                  @Field("for_home") String fromHome,
                                  @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("all_matches") //pass
    Call<ResponseBody> allMatches(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("latitude") double latitude,
                                  @Field("longitude") double longitude,
                                  @Field("search_text") String name,
                                  @Field("from_date") String from,
                                  @Field("to_date") String to,
                                  @Field("club_id") String clubId,
                                  @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("get_player_positions") //pass
    Call<ResponseBody> getPlayerPosition(@Field("language_id") String langId);

    @FormUrlEncoded
    @POST("match_details") //pass
    Call<ResponseBody> matchDetail(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("booking_id") String bookingId,
                                   @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("game_availability_request") //pass
    Call<ResponseBody> gameAvailabilityRequest(@Field("language_id") String langId,
                                                //@Field("user_id") String userId,
                                                @Field("booking_id") String bookingId);

    @FormUrlEncoded
    @POST("formation_teams_check") //pass
    Call<ResponseBody> checkTeamExist(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("booking_id") String bookingId);

    @FormUrlEncoded
    @POST("creating_team") //pass
    Call<ResponseBody> createTeam(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("booking_id") String bookingId,
                                  @Field("team_a") String teamA,
                                  @Field("a_color") String aColor,
                                  @Field("team_b") String teamB,
                                  @Field("b_color") String bColor,
                                  @Field("booking_type") String bookingType);

    @FormUrlEncoded
    @POST("updating_team") //pass
    Call<ResponseBody> updateTeam(@Field("language_id") String langId,
                                  @Field("team_a_id") String teamAId,
                                  @Field("team_b_id") String teamBId,
                                  @Field("team_a_name") String teamA,
                                  @Field("team_a_color") String aColor,
                                  @Field("team_b_name") String teamB,
                                  @Field("team_b_color") String bColor);

    @FormUrlEncoded
    @POST("updating_team") //pass
    Call<ResponseBody> updateTeamImage(@Field("language_id") String langId,
                                      @Field("team_a_id") String teamAId,
                                      @Field("team_b_id") String teamBId,
                                      @Field("team_a_color") String aColor,
                                      @Field("team_b_color") String bColor,
                                      @Field("team_a_image") String aImage,
                                      @Field("team_b_image") String bImage);

    @FormUrlEncoded
    @POST("add_remove_player_to_team") //pass
    Call<ResponseBody> addRemovePlayerFromTeam(@Field("language_id") String langId,
                                              @Field("team_a_id") String teamAId,
                                              @Field("team_b_id") String teamBId,
                                              @Field("target_team_id") String targetTeamId,
                                              @Field("player_id") String playerId,
                                              @Field("is_captain") String isCaptain,
                                              @Field("type") String type);

    @FormUrlEncoded
    @POST("friends_to_teams") //pass
    Call<ResponseBody> friendsToTeams(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("team_a_id") String teamAId,
                                      @Field("team_b_id") String teamBId,
                                      @Field("target_team_id") String targetTeamId,
                                      @Field("player_id") String playerId,
                                      @Field("type") String type);

    @FormUrlEncoded
    @POST("my_friends_list") //pass
    Call<ResponseBody> getManualPlayerList(@Field("language_id") String langId,
                                           @Field("user_id") String userId);

    @Multipart
    @POST("manage_my_friends") //pass
    Call<ResponseBody> manageManualPlayerList(@Part MultipartBody.Part photo,
                                              @Part("language_id") RequestBody langId,
                                              @Part("user_id") RequestBody userId,
                                              @Part("friend_id") RequestBody friendId,
                                              @Part("name") RequestBody name,
                                              @Part("player_position") RequestBody playerPosition,
                                              @Part("type") RequestBody type);

    @FormUrlEncoded
    @POST("manage_my_friends") //pass
    Call<ResponseBody> manageManualPlayerList(@Field("language_id") String langId,
                                              @Field("user_id") String userId,
                                              @Field("friend_id") String friendId,
                                              @Field("name") String name,
                                              @Field("player_position") String position,
                                              @Field("type") String type);

    @FormUrlEncoded
    @POST("remove_friend_photo") //pass
    Call<ResponseBody> removeFriendPhoto(@Field("language_id") String langId,
                                         @Field("user_id") String userId,
                                         @Field("friend_id") String friendId);

    @FormUrlEncoded
    @POST("add_friends_to_booking") //pass
    Call<ResponseBody> addPlayerInGame(@Field("language_id") String langId,
                                       @Field("user_id") String userId,
                                       @Field("booking_id") String bookingId,
                                       @Field("friend_ids") String ids);

    @FormUrlEncoded
    @POST("remove_friends_from_booking") //pass
    Call<ResponseBody> removePlayerFromBooking(@Field("language_id") String langId,
                                               @Field("user_id") String userId,
                                               @Field("booking_id") String bookingId,
                                               @Field("friend_ids") String ids);

    @FormUrlEncoded
    @POST("devices_login_limit") //pass
    Call<ResponseBody> devicesLoginLimit(@Field("user_id") String userId,
                                         @Field("device_id") String deviceId,
                                         @Field("app_name") String appName); //ole

    @FormUrlEncoded
    @POST("remove_manual_players_from_game") //pass
    Call<ResponseBody> removePlayerFromGame(@Field("language_id") String langId,
                                       @Field("booking_id") String bookingId,
                                       @Field("player_id") String id);

    @FormUrlEncoded
    @POST("show_hide_game_to_favorites") //pass
    Call<ResponseBody> showHideFav(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("booking_id") String bookingId,
                                   @Field("is_favorite") String isFavorite);

    @FormUrlEncoded
    @POST("add_remove_player_captain") //pass
    Call<ResponseBody> addRemoveCaptain(@Field("language_id") String langId,
                                            @Field("team_id") String teamId,
                                            @Field("player_id") String pId,
                                            @Field("is_captain") String status);

    @FormUrlEncoded
    @POST("add_game_rating") //pass
    Call<ResponseBody> addGameRating(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("player_id") String playerId,
                                     @Field("booking_id") String bookingId,
                                     @Field("reach_time") String reachTime,
                                     @Field("feedback") String feedback,
                                     @Field("playing_level") int level);

    @FormUrlEncoded
    @POST("get_joined_players") //pass
    Call<ResponseBody> getJoinedPlayers(@Field("language_id") String langId,
                                        @Field("user_id") String userId,
                                        @Field("booking_id") String bookingId);

    @FormUrlEncoded
    @POST("team_players_coordinates") //pass
    Call<ResponseBody> getTeam(@Field("language_id") String langId,
                               @Field("booking_id") String bookingId);

    @FormUrlEncoded
    @POST("save_player_coordinates") //pass
    Call<ResponseBody> saveCoordinates(@Field("language_id") String langId,
                               @Field("positions_data") String data);

    @FormUrlEncoded
    @POST("get_friends_coordinates") //pass
    Call<ResponseBody> getFriendsCoord(@Field("language_id") String langId,
                                       @Field("user_id") String userId,
                                       @Field("booking_id") String bookingId);

    @FormUrlEncoded
    @POST("save_friends_coordinates") //pass
    Call<ResponseBody> saveFriendsCoord(@Field("language_id") String langId,
                                        @Field("positions_data") String data);

    @FormUrlEncoded
    @POST("check_card_discount") //pass
    Call<ResponseBody> cardDiscount(@Field("language_id") String langId,
                                    @Field("user_id") String userId,
                                    @Field("club_id") String clubId,
                                    @Field("booking_price") String bookingPrice,
                                    @Field("booking_date") String bookingDate,
                                    @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("add_new_card") //pass
    Call<ResponseBody> addCard(@Field("language_id") String langId,
                               @Field("user_id") String userId,
                               @Field("number") String number,
                               @Field("expiry") String expiry,
                               @Field("cvv") String cvv,
                               @Field("user_ip") String userIp);

    @FormUrlEncoded
    @POST("user_cards_list") //pass
    Call<ResponseBody> userCards(@Field("language_id") String langId,
                               @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("delete_user_card") //pass
    Call<ResponseBody> deleteCard(@Field("language_id") String langId,
                                 @Field("user_id") String userId,
                                 @Field("card_id") String cardId);

    @FormUrlEncoded
    @POST("player_wallet_details") //pass
    Call<ResponseBody> getWallet(@Field("language_id") String langId,
                                   @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("confirm_player_availablity") //pass
    Call<ResponseBody> confirmPlayer(@Field("language_id") String langId,
                                             @Field("user_id") String userId,
                                             @Field("booking_id") String bookingId,
                                             @Field("player_id") String playerId);

    @FormUrlEncoded
    @POST("check_joining_fee") //pass
    Call<ResponseBody> getMatchFee(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("booking_id") String bookingId,
                                     @Field("is_match") String isFromMatch,
                                     @Field("total_players") String players,
                                     @Field("payment_method") String paymentMethod,
                                    @Field("full_amount") String fullAmount,
                                    @Field("app_module") String appModule);


    @FormUrlEncoded
    @POST("add_amount_to_wallet") //pass
    Call<ResponseBody> addAmountInWallet(@Field("language_id") String langId,
                                         @Field("user_id") String userId,
                                         @Field("order_ref") String orderRef,
                                         @Field("payment_method") String paymentMethod,
                                         @Field("amount") String amount,
                                         @Field("user_ip") String ip);

    @FormUrlEncoded
    @POST("add_booking_payment") //pass
    Call<ResponseBody> addBookingPayment(@Field("language_id") String langId,
                                         @Field("user_id") String userId,
                                         @Field("order_ref") String orderRef,
                                         @Field("payment_method") String paymentMethod,
                                         @Field("amount") String amount,
                                         @Field("wallet_paid") String walletPaid,
                                         @Field("card_paid") String cardPaid,
                                         @Field("booking_id") String bookingId,
                                         @Field("user_ip") String ip);

    @FormUrlEncoded
    @POST("opponent_payment_for_match") //pass
    Call<ResponseBody> opponentPayment(@Field("language_id") String langId,
                                         @Field("user_id") String userId,
                                         @Field("order_ref") String orderRef,
                                         @Field("payment_method") String paymentMethod,
                                         @Field("amount") String amount,
                                         @Field("wallet_paid") String walletPaid,
                                         @Field("card_paid") String cardPaid,
                                         @Field("booking_id") String bookingId,
                                         @Field("user_ip") String ip);

    @FormUrlEncoded
    @POST("match_to_booking") //pass
    Call<ResponseBody> matchToBooking(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("booking_id") String bookingId);

    @FormUrlEncoded
    @POST("check_version") //pass
    Call<ResponseBody> checkUpdate(@Field("type") String type);

    @FormUrlEncoded
    @POST("get_messages") //pass
    Call<ResponseBody> getMessages(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("booking_id") String bookingId);

    @FormUrlEncoded
    @POST("send_message") //pass
    Call<ResponseBody> sendMessage(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("booking_id") String bookingId,
                                   @Field("message") String msg);

    @FormUrlEncoded
    @POST("chat_on_off") //pass
    Call<ResponseBody> chatOnOff(@Field("language_id") String langId,
                                 @Field("user_id") String userId,
                                 @Field("booking_id") String bookingId,
                                 @Field("flag") String flag);

    @FormUrlEncoded
    @POST("get_my_clubs") //pass Main Owner details API
    Call<ResponseBody> getMyClubs(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("get_facilities") //pass
    Call<ResponseBody> getFacilities(@Field("language_id") String langId);

    @Multipart
    @POST      //pass
    Call<ResponseBody> addClub(@Url String url,
                               @Part MultipartBody.Part coverFile,
                               @Part MultipartBody.Part logoFile,
                               @Part("language_id") RequestBody langId,
                               @Part("user_id") RequestBody userId,
                               @Part("name") RequestBody name,
                               @Part("name_ar") RequestBody nameAr,
                               @Part("country_id") RequestBody countryId,
                               @Part("city_id") RequestBody cityId,
                               @Part("facilities") RequestBody facilities,
                               @Part("timings") RequestBody timings,
                               @Part("contact") RequestBody contact,
                               @Part("latlong") RequestBody latLong,
                               @Part("is_flexible") RequestBody flexible,
                               @Part("slots_60") RequestBody slots60,
                               @Part("slots_90") RequestBody slots90,
                               @Part("slots_120") RequestBody slots120,
                               @Part("gap_allowed") RequestBody gapAllowed,
                               @Part("club_id") RequestBody clubId,
                               @Part("club_type") RequestBody clubType);

    @FormUrlEncoded
    @POST("add_field") //pass
    Call<ResponseBody> addField(@Field("language_id") String langId,
                                @Field("user_id") String userId,
                                @Field("club_id") String clubId,
                                @Field("name") String name,
                                @Field("field_type") String fieldType,
                                @Field("field_size") String fieldSize,
                                @Field("grass_type") String grassType,
                                @Field("field_color") String fieldColor,
                                @Field("is_merge") String isMerge,
                                @Field("field1_id") String field1Id,
                                @Field("field2_id") String field2Id,
                                @Field("field3_id") String field3Id,
                                @Field("days_price") String daysPrice);

    @Multipart
    @POST("api/padel/fields/add") //pass
    Call<ResponseBody> addPadelField(@Part MultipartBody.Part coverFile,
                                   @Part("language_id") RequestBody langId,
                                   @Part("user_id") RequestBody userId,
                                   @Part("club_id") RequestBody clubId,
                                   @Part("name") RequestBody name,
                                   @Part("field_type") RequestBody fieldType,
                                   @Part("field_color") RequestBody color,
                                   @Part("days_price") RequestBody price);

    @FormUrlEncoded
    @POST("delete_field") //pass
    Call<ResponseBody> deleteField(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("field_id") String fieldId);

    @FormUrlEncoded
    @POST("update_field") //pass
    Call<ResponseBody> updateField(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("club_id") String clubId,
                                   @Field("field_id") String fieldId,
                                   @Field("name") String name,
                                   @Field("field_type") String fieldType,
                                   @Field("field_size") String fieldSize,
                                   @Field("grass_type") String grassType,
                                   @Field("field_color") String fieldColor,
                                   @Field("days_price") String daysPrice);

    @Multipart
    @POST("update_field") //pass
    Call<ResponseBody> updatePadelField(@Part MultipartBody.Part coverFile,
                                     @Part("language_id") RequestBody langId,
                                     @Part("user_id") RequestBody userId,
                                     @Part("club_id") RequestBody clubId,
                                     @Part("field_id") RequestBody fieldId,
                                     @Part("name") RequestBody name,
                                     @Part("field_type") RequestBody fieldType,
                                     @Part("field_color") RequestBody color,
                                     @Part("days_price") RequestBody price);

    @FormUrlEncoded
    @POST("get_club") //pass
    Call<ResponseBody> getClub(@Field("language_id") String langId,
                               @Field("user_id") String userId,
                               @Field("club_id") String clubId);

    @FormUrlEncoded
    @POST("delete_club") //pass
    Call<ResponseBody> deleteClub(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("club_id") String clubId);

    @FormUrlEncoded
    @POST("hidden_list") //pass
    Call<ResponseBody> hiddenFields(@Field("language_id") String langId,
                                    @Field("user_id") String userId,
                                    @Field("club_id") String clubId,
                                    @Field("field_id") String fieldId);

    @FormUrlEncoded
    @POST("hiding_slots") //pass
    Call<ResponseBody> hideSlots(@Field("language_id") String langId,
                                 @Field("user_id") String userId,
                                 @Field("club_id") String clubId,
                                 @Field("field_id") String fieldId,
                                 @Field("slots") String slots,
                                 @Field("date") String date,
                                 @Field("hide_type") String hideType,
                                 @Field("hide_price") String hidePrice,
                                 @Field("hide_reason") String hideReason);

    @FormUrlEncoded
    @POST("delete_hidden_slots") //pass
    Call<ResponseBody> unhideAllSlots(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("club_id") String clubId,
                                      @Field("field_id") String fieldId,
                                      @Field("date") String date,
                                      @Field("hide_type") String hideType);

    @FormUrlEncoded
    @POST("delete_booking") //pass
    Call<ResponseBody> deleteBooking(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("booking_id") String bookingId);

    @FormUrlEncoded
    @POST("hidden_slots_details") //pass
    Call<ResponseBody> hiddenSlotDetails(@Field("language_id") String langId,
                                         @Field("user_id") String userId,
                                         @Field("slot_ids") String slotIds);

    @FormUrlEncoded
    @POST("user_bookings") //pass
    Call<ResponseBody> userBookings(@Field("language_id") String langId,
                                    @Field("user_id") String userId,
                                    @Field("from") String from,
                                    @Field("to") String to,
                                    @Field("club_id") String clubId,
                                    @Field("field_id") String fieldId,
                                    @Field("is_needed") String isNeeded,
                                    @Field("date") String date,
                                    @Field("type") String type);
    @FormUrlEncoded
    @POST("last_or_next_week_bookings") //pass
    Call<ResponseBody> getPastAndFutureBookingsAPI(@Field("language_id") String langId,
                                                   @Field("club_id") String clubId,
                                                   @Field("field_id") String fieldId,
                                                   @Field("date") String date,
                                                   @Field("type") String type);


    @FormUrlEncoded
    @POST("schedule_list") //pass
    Call<ResponseBody> getScheduleList(@Field("language_id") String langId,
                                       @Field("club_id") String clubId,
                                       @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("user_schedules") //pass
    Call<ResponseBody> getScheduleDetail(@Field("language_id") String langId,
                                         @Field("user_id") String userId,
                                         @Field("ids") String ids,
                                         @Field("from_date") String fromDate,
                                         @Field("to_date") String toDate);

    @FormUrlEncoded
    @POST("update_schedule_price") //pass
    Call<ResponseBody> updateSchedulePrice(@Field("language_id") String langId,
                                           @Field("user_id") String userId,
                                           @Field("ids") String ids,
                                           @Field("name") String name,
                                           @Field("phone") String phone,
                                           @Field("price_plus") String pricePlus,
                                           @Field("price_minus") String priceMinus);
    @FormUrlEncoded
    @POST("remove_selected_schedule") //pass
    Call<ResponseBody> removeSelectedSchedule(@Field("language_id") String langId,
                                               @Field("ids") String ids);


    @FormUrlEncoded
    @POST("delete_schedules") //pass
    Call<ResponseBody> deleteSchedule(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("ids") String ids);

    @FormUrlEncoded
    @POST("block_unblock_user") //pass
    Call<ResponseBody> blockUnblockUser(@Field("language_id") String langId,
                                        @Field("owner_id") String ownerId,
                                        @Field("player_id") String playerId,
                                        @Field("flag") String flag,
                                        @Field("reason") String reason,
                                        @Field("user_phone") String phone);

    @FormUrlEncoded
    @POST("single_booking") //pass
    Call<ResponseBody> getBookingDetail(@Field("language_id") String langId,
                                        @Field("user_id") String userId,
                                        @Field("booking_id") String bookingId);

    @FormUrlEncoded
    @POST("update_call_booking") //pass
    Call<ResponseBody> updateCallBookingDetail(@Field("language_id") String langId,
                                               @Field("user_id") String userId,
                                               @Field("booking_id") String bookingId,
                                               @Field("name") String name,
                                               @Field("phone") String phone,
                                               @Field("price_plus") String pricePlus,
                                               @Field("price_minus") String priceMinus);

    @FormUrlEncoded
    @POST("add_target_bookings_discount") //pass
    Call<ResponseBody> addDiscountCard(@Field("language_id") String langId,
                                       @Field("title") String title,
                                       @Field("user_id") String userId,
                                       @Field("club_id") String clubId,
                                       @Field("bookings_required") String bookingsRequired,
                                       @Field("discount_type") String discountType,
                                       @Field("discount_value") String discountValue,
                                       @Field("from_date") String from,
                                       @Field("to_date") String to);

    @FormUrlEncoded
    @POST("get_target_bookings_discount") //pass
    Call<ResponseBody> getDiscountCards(@Field("language_id") String langId,
                                        @Field("user_id") String userId,
                                        @Field("club_id") String clubId,
                                        @Field("status") String status);

    @FormUrlEncoded
    @POST("delete_target_bookings_discount") //pass
    Call<ResponseBody> deleteDiscountCard(@Field("language_id") String langId,
                                          @Field("user_id") String userId,
                                          @Field("discount_id") String discountId);

    @FormUrlEncoded
    @POST("update_target_bookings_discount") //pass
    Call<ResponseBody> updateDiscountCard(@Field("language_id") String langId,
                                          @Field("title") String title,
                                          @Field("user_id") String userId,
                                          @Field("club_id") String clubId,
                                          @Field("bookings_required") String bookingsRequired,
                                          @Field("discount_type") String discountType,
                                          @Field("discount_value") String discountValue,
                                          @Field("from_date") String from,
                                          @Field("to_date") String to,
                                          @Field("discount_id") String discountId);

    @Multipart
    @POST("add_coupon") //pass
    Call<ResponseBody> addCoupon(@Part("language_id") RequestBody langId,
                                 @Part("coupon_title") RequestBody title,
                                 @Part("user_id") RequestBody userId,
                                 @Part("club_id") RequestBody clubId,
                                 @Part("field_ids") RequestBody fieldIds,
                                 @Part("coupon_code") RequestBody code,
                                 @Part("usage_limit") RequestBody limit,
                                 @Part("discount_type") RequestBody discountType,
                                 @Part("discount") RequestBody discountValue,
                                 @Part("one_hour_discount") RequestBody oneHourDiscount,
                                 @Part("one_half_hour_discount") RequestBody oneHalfHourDiscount,
                                 @Part("two_hour_discount") RequestBody twoHourDiscount,
                                 @Part("from_date") RequestBody from,
                                 @Part("to_date") RequestBody to,
                                 @Part("player_usage_limit") RequestBody playerLimit,
                                 @Part("player_ids") RequestBody playerIds,
                                 @Part MultipartBody.Part coverFile);

    @Multipart
    @POST("update_coupon") //pass
    Call<ResponseBody> updateCoupon(@Part("language_id") RequestBody langId,
                                    @Part("coupon_title") RequestBody title,
                                    @Part("user_id") RequestBody userId,
                                    @Part("club_id") RequestBody clubId,
                                    @Part("field_ids") RequestBody fieldIds,
                                    @Part("coupon_code") RequestBody code,
                                    @Part("usage_limit") RequestBody limit,
                                    @Part("discount_type") RequestBody discountType,
                                    @Part("discount") RequestBody discountValue,
                                    @Part("one_hour_discount") RequestBody oneHourDiscount,
                                    @Part("one_half_hour_discount") RequestBody oneHalfHourDiscount,
                                    @Part("two_hour_discount") RequestBody twoHourDiscount,
                                    @Part("from_date") RequestBody from,
                                    @Part("to_date") RequestBody to,
                                    @Part("coupon_id") RequestBody couponId,
                                    @Part("player_usage_limit") RequestBody playerLimit,
                                    @Part("player_ids") RequestBody playerIds,
                                    @Part MultipartBody.Part coverFile);

    @FormUrlEncoded
    @POST("delete_coupons") //pass
    Call<ResponseBody> deleteCoupon(@Field("language_id") String langId,
                                    @Field("user_id") String userId,
                                    @Field("type") String type,
                                    @Field("coupon_id") String couponId);

    @FormUrlEncoded
    @POST("get_coupons") //pass
    Call<ResponseBody> getCoupons(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("club_id") String clubId,
                                  @Field("status") String status);

    @FormUrlEncoded
    @POST("add_offer_new") //pass
    Call<ResponseBody> addOffer(@Field("language_id") String langId,
                                @Field("name") String title,
                                @Field("user_id") String userId,
                                @Field("club_id") String clubId,
                                @Field("field_id") String fieldIds,
                                @Field("timing_start") String startTime,
                                @Field("timing_end") String endTime,
                                @Field("day_id") String dayId,
                                @Field("discount_type") String discountType,
                                @Field("discount_value") String discountValue,
                                @Field("from_date") String from,
                                @Field("to_date") String to);

    @FormUrlEncoded
    @POST("get_offers") //pass
    Call<ResponseBody> getOffers(@Field("language_id") String langId,
                                 @Field("user_id") String userId,
                                 @Field("club_id") String clubId,
                                 @Field("status") String status);

    @FormUrlEncoded
    @POST("owner_promotions_list") //pass
    Call<ResponseBody> ownerPromotionsList(@Field("language_id") String langId,
                                           @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("delete_offer") //pass
    Call<ResponseBody> deleteOffer(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("offer_id") String offerId);

    @FormUrlEncoded
    @POST("players_stats_listing") //pass
    Call<ResponseBody> getBookingsCount(@Field("language_id") String langId,
                                        @Field("user_id") String userId,
                                        @Field("from") String from,
                                        @Field("to") String to,
                                        @Field("filter") String filter,
                                        @Field("days") String days,
                                        @Field("search") String search,
                                        @Field("club_id") String clubId,
                                        @Field("limits") int pageNo);

    @FormUrlEncoded
    @POST("player_bookings_detail") //pass
    Call<ResponseBody> getBookingsCountDetail(@Field("language_id") String langId,
                                              @Field("user_id") String userId,
                                              @Field("club_id") String clubId,
                                              @Field("player_id") String playerId,
                                              @Field("phone") String phone,
                                              @Field("type") String type,
                                              @Field("from") String from,
                                              @Field("to") String to,
                                              @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("bookings_list_player_stats") //pass
    Call<ResponseBody> bookingListPlayerStat(@Field("language_id") String langId,
                                             @Field("user_id") String userId,
                                             @Field("club_id") String clubId,
                                             @Field("player_id") String playerId,
                                             @Field("user_phone") String playerPhone,
                                             @Field("type") String type,
                                             @Field("from") String from,
                                             @Field("to") String to);

    @FormUrlEncoded
    @POST("player_stats_details") //pass
    Call<ResponseBody> profileForOwner(@Field("language_id") String langId,
                                       @Field("user_id") String userId,
                                       @Field("club_id") String clubId,
                                       @Field("player_id") String playerId,
                                       @Field("user_phone") String playerPhone);

    @FormUrlEncoded
    @POST("customer_count_for_sms") //pass
    Call<ResponseBody> customerCountForSms(@Field("language_id") String langId,
                                           @Field("user_id") String userId,
                                           @Field("club_id") String clubId);

    @FormUrlEncoded
    @POST("sms_request_for_players") //pass
    Call<ResponseBody> sendSMS(@Field("language_id") String langId,
                               @Field("user_id") String userId,
                               @Field("club_id") String clubId,
                               @Field("sms_for") String smsFor,
                               @Field("sms_text") String smsStr,
                               @Field("total_sms") String totalSms,
                               @Field("send_date") String date,
                               @Field("send_time") String time,
                               @Field("amount") String amount,
                               @Field("payment_method") String paymentMethod,
                               @Field("user_ip") String userIp,
                               @Field("order_ref") String orderRef);

    @FormUrlEncoded
    @POST("sms_details") //pass
    Call<ResponseBody> getSMS(@Field("language_id") String langId,
                              @Field("user_id") String userId,
                              @Field("club_id") String clubId);

    @FormUrlEncoded
    @POST("get_blocked_users") //pass
    Call<ResponseBody> blockedUser(@Field("language_id") String langId,
                                   @Field("owner_id") String userId);

    @FormUrlEncoded
    @POST("update_sms_data") //pass
    Call<ResponseBody> updateSMS(@Field("language_id") String langId,
                                @Field("user_id") String userId,
                                @Field("club_id") String clubId,
                                @Field("sms_id") String smsId,
                                @Field("sms_text") String msg,
                                @Field("send_date") String date,
                                @Field("send_time") String time,
                                @Field("for_owner") String owner,
                                @Field("for_players") String players,
                                @Field("for_gap") String gap,
                                @Field("for_continuous") String booking);

    @FormUrlEncoded
    @POST("add_update_payment_details") //pass
    Call<ResponseBody> updatePaymentSettings(@Field("language_id") String langId,
                                             @Field("user_id") String userId,
                                             @Field("club_id") String clubId,
                                             @Field("is_cash") String cash,
                                             @Field("is_card") String card,
                                             @Field("bank_name") String bankName,
                                             @Field("account_name") String accountName,
                                             @Field("iban_number") String iban,
                                             @Field("account_number") String accountNo,
                                             @Field("bank_branch") String branch);

    @FormUrlEncoded
    @POST("get_payment_details") //pass
    Call<ResponseBody> getPaymentDetails(@Field("language_id") String langId,
                                         @Field("user_id") String userId,
                                         @Field("club_id") String clubId);

    @FormUrlEncoded
    @POST("get_featured")
    Call<ResponseBody> getFeaturedClub(@Field("language_id") String langId,
                                       @Field("user_id") String userId,
                                       @Field("club_id") String clubId);

    @FormUrlEncoded
    @POST("add_featured_club")
    Call<ResponseBody> addFeaturedClub(@Field("language_id") String langId,
                                       @Field("user_id") String userId,
                                       @Field("club_id") String clubId,
                                       @Field("from") String from,
                                       @Field("to") String to,
                                       @Field("price") String price,
                                       @Field("cities") String cities,
                                       @Field("featured_id") String featuredId);

    @FormUrlEncoded
    @POST("owner_earnings")
    Call<ResponseBody> getOwnerEarnings(@Field("language_id") String langId,
                                        @Field("user_id") String userId,
                                        @Field("club_id") String clubId,
                                        @Field("field_id") String fieldId,
                                        @Field("from") String from,
                                        @Field("to") String to);

    @FormUrlEncoded
    @POST("owner_card_earnings")
    Call<ResponseBody> getOwnerCardEarnings(@Field("language_id") String langId,
                                            @Field("user_id") String userId,
                                            @Field("from") String from,
                                            @Field("to") String to,
                                            @Field("club_id") String clubId,
                                            @Field("field_id") String fieldId,
                                            @Field("type") String type);

    @FormUrlEncoded
    @POST("earnings_report")
    Call<ResponseBody> exportEarnings(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("club_id") String clubId,
                                      @Field("field_id") String fieldId,
                                      @Field("from") String from,
                                      @Field("to") String to,
                                      @Field("email") String email);

    @FormUrlEncoded
    @POST("get_earning_details")
    Call<ResponseBody> getEarningsDetails(@Field("language_id") String langId,
                                          @Field("user_id") String userId,
                                          @Field("booking_id") String bookingId,
                                          @Field("type") String type);

    @FormUrlEncoded
    @POST("permissions_list")
    Call<ResponseBody> getPermissions(@Field("language_id") String langId);

    @FormUrlEncoded
    @POST("add_update_roles")
    Call<ResponseBody> addUserRole(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("role_name") String title,
                                   @Field("perm_ids") String ids,
                                   @Field("role_id") String roleId);

    @FormUrlEncoded
    @POST("roles_list")
    Call<ResponseBody> getUserRole(@Field("language_id") String langId,
                                   @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("delete_role")
    Call<ResponseBody> deleteUserRole(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("role_id") String roleId);

    @Multipart
    @POST("add_update_employees")
    Call<ResponseBody> addEmployee(@Part MultipartBody.Part filePart,
                                   @Part("language_id") RequestBody langId,
                                   @Part("user_id") RequestBody userId,
                                   @Part("employee_name") RequestBody employeeName,
                                   @Part("employee_phone") RequestBody employeePhone,
                                   @Part("employee_email") RequestBody employeeEmail,
                                   @Part("employee_password") RequestBody employeePassword,
                                   @Part("role_id") RequestBody roleId,
                                   @Part("employee_id") RequestBody employeeId,
                                   @Part("club_id") RequestBody clubId,
                                   @Part("employee_salary") RequestBody employeeSalary,
                                   @Part("id") RequestBody id);

    @FormUrlEncoded
    @POST("employees_list")
    Call<ResponseBody> getEmployees(@Field("language_id") String langId,
                                    @Field("user_id") String userId,
                                    @Field("club_id") String clubId,
                                    @Field("sorting") String sorting);

    @FormUrlEncoded
    @POST("delete_employee")
    Call<ResponseBody> deleteEmployee(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("employee_id") String employeeId);

    @FormUrlEncoded
    @POST("get_employee")
    Call<ResponseBody> getEmployee(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("employee_id") String employeeId);

    @FormUrlEncoded
    @POST("user_logout")
    Call<ResponseBody> logout(@Field("language_id") String langId,
                              @Field("user_id") String userId,
                              @Field("device_id") String deviceId);

    @FormUrlEncoded
    @POST("wallet_with_payment_method")
    Call<ResponseBody> getClubPaymentMethod(@Field("language_id") String langId,
                                            @Field("user_id") String userId,
                                            @Field("club_id") String clubId,
                                            @Field("app_module") String module);

    @FormUrlEncoded
    @POST("notifications_and_promotions")
    Call<ResponseBody> getPromotions(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("total_completed_details")
    Call<ResponseBody> totalCompleteBookings(@Field("language_id") String langId,
                                             @Field("owner_id") String userId,
                                             @Field("club_id") String clubId,
                                             @Field("player_id") String playerId,
                                             @Field("is_call") String isCall,
                                             @Field("this_month") String thisMonth,
                                             @Field("call_booking_phone") String phone,
                                             @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("create_order_request")
    Call<ResponseBody> createOrderRequest(@Field("language_id") String langId,
                                          @Field("user_id") String userId,
                                          @Field("b_id") String bookingId,
                                          @Field("price") String price,
                                          @Field("currency") String currency);

    @FormUrlEncoded
    @POST("check_payment_status")
    Call<ResponseBody> checkPaymentStatus(@Field("language_id") String langId,
                                          @Field("user_id") String userId,
                                          @Field("b_id") String bookingId,
                                          @Field("order_ref") String orderRef);

    @FormUrlEncoded
    @POST("api/shop/home")
    Call<ResponseBody> getShopHome(@Field("language_id") String langId,
                                   @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("api/shop/categories")
    Call<ResponseBody> getCategories(@Field("language_id") String langId,
                                     @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("api/shop/sub_categories")
    Call<ResponseBody> getSubCategories(@Field("language_id") String langId,
                                        @Field("category_id") String catId);

    @FormUrlEncoded
    @POST("api/shop/brands")
    Call<ResponseBody> getBrands(@Field("language_id") String langId,
                                 @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("api/shop/shop_deals")
    Call<ResponseBody> getShopDeals(@Field("language_id") String langId,
                                    @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("api/shop/products_list")
    Call<ResponseBody> getProducts(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("type_id") String typeId,
                                   @Field("keyword") String keyword,
                                   @Field("type") String type,
                                   @Field("order_by") String orderBy,
                                   @Field("filters") String filters,
                                   @Field("min_price") String minPrice,
                                   @Field("max_price") String maxPrice,
                                   @Field("limits") int pageNo,
                                   @Field("sub_category_id") String subCatId);

    @FormUrlEncoded
    @POST("api/shop/wishlist/add")
    Call<ResponseBody> addWishlist(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("product_id") String productId);

    @FormUrlEncoded
    @POST("api/shop/wishlist/delete")
    Call<ResponseBody> removeWishlist(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("product_id") String productId);

    @FormUrlEncoded
    @POST("api/shop/product_detail")
    Call<ResponseBody> getProduct(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("product_id") String productId);

    @FormUrlEncoded
    @POST("api/shop/cart/add")
    Call<ResponseBody> addToCart(@Field("language_id") String langId,
                                 @Field("user_id") String userId,
                                 @Field("product_id") String productId,
                                 @Field("quantity") int quantity,
                                 @Field("color") String color,
                                 @Field("combination_id") String combinationId,
                                 @Field("variants") String variants);

    @FormUrlEncoded
    @POST("api/shop/cart/get")
    Call<ResponseBody> getCart(@Field("language_id") String langId,
                               @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("api/shop/cart/delete")
    Call<ResponseBody> deleteCart(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("product_id") String prodId,
                                  @Field("cart_id") String cartId);

    @FormUrlEncoded
    @POST("api/shop/cart/update")
    Call<ResponseBody> updateCart(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("product_id") String prodId,
                                  @Field("cart_id") String cartId,
                                  @Field("quantity") int qty);

    @FormUrlEncoded
    @POST("api/shop/address/get")
    Call<ResponseBody> getAddress(@Field("language_id") String langId,
                                  @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("api/shop/address/delete")
    Call<ResponseBody> deleteAddress(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("address_id") String addressId);

    @FormUrlEncoded
    @POST("api/shop/address/update")
    Call<ResponseBody> updateAddress(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("address_id") String addressId,
                                     @Field("name") String name,
                                     @Field("phone") String phone,
                                     @Field("address") String address,
                                     @Field("city") String city,
                                     @Field("area") String area,
                                     @Field("number") String number,
                                     @Field("is_home") String isHome,
                                     @Field("delivery_note") String deliveryNote);

    @FormUrlEncoded
    @POST("api/shop/address/add")
    Call<ResponseBody> addAddress(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("name") String name,
                                  @Field("phone") String phone,
                                  @Field("latitude") double latitude,
                                  @Field("longitude") double longitude,
                                  @Field("address") String address,
                                  @Field("city") String city,
                                  @Field("area") String area,
                                  @Field("number") String number,
                                  @Field("is_home") String isHome,
                                  @Field("delivery_note") String deliveryNote);

    @FormUrlEncoded
    @POST("shop/api/order/store")
    Call<ResponseBody> placeOrder(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("address_id") String addressId,
                                  @Field("grand_total") double grandTotal,
                                  @Field("shipping_cost") double shippingCost,
                                  @Field("cod") double cod,
                                  @Field("payment_type") String paymentType,
                                  @Field("order_reference") String orderRef,
                                  @Field("coupon_code") String code,
                                  @Field("coupon_discount") double discount,
                                  @Field("wallet_paid") String walletPaid,
                                  @Field("card_paid") String cardPaid,
                                  @Field("user_ip") String ip);

    @FormUrlEncoded
    @POST("api/shop/get_shipping_fee")
    Call<ResponseBody> getShippingFee(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("city_id") String cityId);

    @FormUrlEncoded
    @POST("shop/api/coupon/apply")
    Call<ResponseBody> getCouponDis(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("code") String code);

    @FormUrlEncoded
    @POST("api/shop/orders/get")
    Call<ResponseBody> getOrders(@Field("language_id") String langId,
                                 @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("api/shop/orders/detail")
    Call<ResponseBody> getOrderDetail(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("order_id") String orderId);

    @FormUrlEncoded
    @POST("api/shop/orders/cancel")
    Call<ResponseBody> cancelOrder(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("order_id") String orderId);

    @FormUrlEncoded
    @POST("api/shop/reviews/add")
    Call<ResponseBody> orderReview(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("product_id") String productId,
                                   @Field("stars") float rating,
                                   @Field("comment") String msg);

    @FormUrlEncoded
    @POST("api/shop/find")
    Call<ResponseBody> search(@Field("language_id") String langId,
                              @Field("user_id") String userId,
                              @Field("keyword") String keyword);

    @FormUrlEncoded
    @POST("api/shop/wishlist/get")
    Call<ResponseBody> getWishlist(@Field("language_id") String langId,
                                   @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("api/padel/add_players")
    Call<ResponseBody> addPartner(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("booking_id") String bookingId,
                                   @Field("players") String players,
                                   @Field("type") String type);

    @FormUrlEncoded
    @POST("api/padel/remove_players")
    Call<ResponseBody> removePartner(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("booking_id") String bookingId,
                                  @Field("players") String players);

    @FormUrlEncoded
    @POST("api/padel/payment_status")
    Call<ResponseBody> updatePaymentStatus(@Field("language_id") String langId,
                                         @Field("user_id") String userId,
                                         @Field("booking_id") String bookingId,
                                         @Field("players") String players,
                                           @Field("paid_amount") String paidAmount,
                                           @Field("payment_method") String paymentMethod,
                                           @Field("user_ip") String userIp,
                                           @Field("order_ref") String orderRef,
                                           @Field("card_paid") String cardPaid,
                                           @Field("wallet_paid") String walletPaid);

    @FormUrlEncoded
    @POST("api/padel/reject_invitation")
    Call<ResponseBody> rejectInvitation(@Field("language_id") String langId,
                                        @Field("user_id") String userId,
                                        @Field("booking_id") String bookingId);

    @FormUrlEncoded
    @POST("api/padel/accept_invitation")
    Call<ResponseBody> acceptInvitation(@Field("language_id") String langId,
                                        @Field("user_id") String userId,
                                        @Field("booking_id") String bookingId,
                                        @Field("amount") String amount,
                                        @Field("payment_method") String paymentMethod,
                                        @Field("user_ip") String userIp,
                                        @Field("order_ref") String orderRef,
                                        @Field("card_paid") String cardPaid,
                                        @Field("wallet_paid") String walletPaid);

    @FormUrlEncoded
    @POST("api/padel/assign_to_other")
    Call<ResponseBody> assignToOther(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("booking_id") String bookingId,
                                     @Field("player_id") String playerId,
                                     @Field("type") String type);

    @FormUrlEncoded
    @POST("api/padel/create_match")
    Call<ResponseBody> createPadelMatch(@Field("language_id") String langId,
                                       @Field("user_id") String userId,
                                       @Field("booking_id") String bookingId,
                                       @Field("partner_id") String partnerId,
                                       @Field("min_age") String minAge,
                                       @Field("max_age") String maxAge,
                                       @Field("order_ref") String orderRef,
                                        @Field("card_paid") String cardPaid,
                                        @Field("wallet_paid") String walletPaid,
                                        @Field("padel_level_id") String padelLevelId,
                                       @Field("payment_method") String paymentMethod,
                                       @Field("user_ip") String ip);

    @FormUrlEncoded
    @POST("api/padel/get_padel_levels")
    Call<ResponseBody> getLevels(@Field("language_id") String langId,
                                 @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("api/padel/change_partner")
    Call<ResponseBody> changePartner(@Field("language_id") String langId,
                                        @Field("user_id") String userId,
                                        @Field("booking_id") String bookingId,
                                        @Field("partner_id") String partnerId,
                                        @Field("old_partner_id") String oldPartnerId);

    @FormUrlEncoded
    @POST("api/padel/challenge_padel")
    Call<ResponseBody> challengePadel(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("booking_id") String bookingId,
                                      @Field("partner_id") String partnerId,
                                      @Field("order_ref") String orderRef,
                                      @Field("card_paid") String cardPaid,
                                      @Field("wallet_paid") String walletPaid,
                                      @Field("joining_fee") String joiningFee,
                                      @Field("padel_level_id") String padelLevelId,
                                      @Field("user_ip") String userIp,
                                      @Field("payment_method") String paymentMethod);

    @FormUrlEncoded
    @POST("api/padel/cancel_match")
    Call<ResponseBody> cancelMatch(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("booking_id") String bookingId);

    @FormUrlEncoded
    @POST("api/padel/cancel_request")
    Call<ResponseBody> cancelRequest(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("booking_id") String bookingId);

    @FormUrlEncoded
    @POST("api/padel/cancel_accepted_challenge")
    Call<ResponseBody> cancelAcceptedChallenge(@Field("language_id") String langId,
                                               @Field("user_id") String userId,
                                               @Field("booking_id") String bookingId,
                                               @Field("request_by") String requestBy);

    @FormUrlEncoded
    @POST("api/padel/accept_challenge")
    Call<ResponseBody> acceptChallenge(@Field("language_id") String langId,
                                       @Field("user_id") String userId,
                                       @Field("booking_id") String bookingId,
                                       @Field("request_by") String requestBy);

    @FormUrlEncoded
    @POST("api/padel/cancel_challenge")
    Call<ResponseBody> cancelChallenge(@Field("language_id") String langId,
                                       @Field("user_id") String userId,
                                       @Field("booking_id") String bookingId,
                                       @Field("request_by") String requestBy);

    @FormUrlEncoded
    @POST("api/padel/classifications")
    Call<ResponseBody> classifications(@Field("language_id") String langId,
                                       @Field("user_id") String userId);

    @Multipart
    @POST("api/padel/add_document")
    Call<ResponseBody> uploadDocument(@Part MultipartBody.Part file,
                                      @Part("language_id") RequestBody langId,
                                      @Part("user_id") RequestBody userId);

    @FormUrlEncoded
    @POST("slots_and_payment")
    Call<ResponseBody> slotAndPayment(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("club_id") String clubId,
                                      @Field("players") String players,
                                      @Field("days") String days,
                                      @Field("payment_method") String paymentMethod);

    @FormUrlEncoded
    @POST("slots_and_payment_list")
    Call<ResponseBody> slotAndPaymentList(@Field("language_id") String langId,
                                          @Field("user_id") String userId,
                                          @Field("club_id") String clubId);

    @FormUrlEncoded
    @POST("remove_from_restriction")
    Call<ResponseBody> removeFromRestriction(@Field("language_id") String langId,
                                          @Field("user_id") String userId,
                                          @Field("club_id") String clubId,
                                          @Field("player_id") String playerId,
                                             @Field("days") String days);

    @FormUrlEncoded
    @POST("update_player_payment")
    Call<ResponseBody> updatePlayerPayment(@Field("language_id") String langId,
                                           @Field("user_id") String userId,
                                           @Field("club_id") String clubId,
                                           @Field("player_id") String players,
                                           @Field("payment_method") String paymentMethod);

    @FormUrlEncoded
    @POST("update_player_payment")
    Call<ResponseBody> updatePlayerPayment(@Field("language_id") String langId,
                                           @Field("user_id") String userId,
                                           @Field("club_id") String clubId,
                                           @Field("player_id") String players,
                                           @Field("days") String days,
                                           @Field("payment_method") String paymentMethod);

    @FormUrlEncoded
    @POST("manage_bookings_restriction")
    Call<ResponseBody> manageBookingsRestriction(@Field("language_id") String langId,
                                                 @Field("user_id") String userId,
                                                 @Field("club_id") String clubId,
                                                 @Field("player_id") String playerId,
                                                 @Field("player_phone") String playerPhone,
                                                 @Field("bookings") String bookings);

    @FormUrlEncoded
    @POST("manage_player_continuous")
    Call<ResponseBody> managePlayerContinuous(@Field("language_id") String langId,
                                              @Field("user_id") String userId,
                                              @Field("club_id") String clubId,
                                              @Field("player_id") String playerId,
                                              @Field("flag") String flag);

    @FormUrlEncoded
    @POST("profile_level_details")
    Call<ResponseBody> profileLevelDetails(@Field("language_id") String langId,
                                           @Field("user_id") String userId,
                                           @Field("level_id") String levelId);

    @FormUrlEncoded
    @POST("ole_missions")
    Call<ResponseBody> oelMission(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("cashback_to_player")
    Call<ResponseBody> cashbackToPlayer(@Field("language_id") String langId,
                                        @Field("user_id") String userId,
                                        @Field("level_id") String levelId,
                                        @Field("amount") String amount,
                                        @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("api/groups/get")
    Call<ResponseBody> getGroupList(@Field("language_id") String langId,
                                    @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("api/groups/remove")
    Call<ResponseBody> deleteGroup(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("group_id") String groupId);

    @FormUrlEncoded
    @POST("api/groups/add")
    Call<ResponseBody> addGroup(@Field("language_id") String langId,
                                @Field("user_id") String userId,
                                @Field("name") String name);
    @FormUrlEncoded
    @POST("api/groups/details")
    Call<ResponseBody> getGroup(@Field("language_id") String langId,
                                @Field("user_id") String userId,
                                @Field("group_id") String groupId);

    @FormUrlEncoded
    @POST("api/groups/add_player")
    Call<ResponseBody> addPlayerGroup(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("group_id") String groupId,
                                      @Field("players") String players);

    @FormUrlEncoded
    @POST("api/groups/remove_player")
    Call<ResponseBody> removePlayerGroup(@Field("language_id") String langId,
                                         @Field("user_id") String userId,
                                         @Field("group_id") String groupId,
                                         @Field("player_id") String playerId);

    @FormUrlEncoded
    @POST("levels_target_status")
    Call<ResponseBody> levelsTargetStatus(@Field("language_id") String langId,
                                          @Field("user_id") String userId,
                                          @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("card_discount_target_status")
    Call<ResponseBody> loyaltyTargetStatus(@Field("language_id") String langId,
                                           @Field("user_id") String userId,
                                           @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("dismiss_target_status")
    Call<ResponseBody> dismissTargetStatus(@Field("language_id") String langId,
                                           @Field("user_id") String userId,
                                           @Field("dismiss_id") String dismissId);

    @FormUrlEncoded
    @POST("add_football_score")
    Call<ResponseBody> addFootballScore(@Field("language_id") String langId,
                                        @Field("user_id") String userId,
                                        @Field("player_one") String p1Id,
                                        @Field("player_two") String p2Id,
                                        @Field("booking_id") String bookingId,
                                        @Field("one_goals") String p1Goal,
                                        @Field("two_goals") String p2Goal);

    @FormUrlEncoded
    @POST("api/padel/add_padel_score")
    Call<ResponseBody> addPadelScore(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("booking_id") String bookingId,
                                     @Field("creator_id") String creatorId,
                                     @Field("player_two_id") String p2Id,
                                     @Field("creator_set_one") String p1Set1,
                                     @Field("creator_set_two") String p1Set2,
                                     @Field("creator_set_three") String p1Set3,
                                     @Field("player_two_set_one") String p2Set1,
                                     @Field("player_two_set_two") String p2Set2,
                                     @Field("player_two_set_three") String p2Set3);

    @FormUrlEncoded
    @POST("get_all_slots")
    Call<ResponseBody> getAllSlots(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("field_id") String fieldId,
                                   @Field("club_id") String clubId,
                                   @Field("day_id") String dayId);

    @FormUrlEncoded
    @POST("save_ladies_slots")
    Call<ResponseBody> saveLadiesSlots(@Field("language_id") String langId,
                                       @Field("user_id") String userId,
                                       @Field("field_id") String fieldId,
                                       @Field("club_id") String clubId,
                                       @Field("day_id") String days,
                                       @Field("slots") String slots);

    @FormUrlEncoded
    @POST("ole_plans")
    Call<ResponseBody> olePlans(@Field("language_id") String langId,
                                @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("get_discount_for_plans")
    Call<ResponseBody> planCouponDiscount(@Field("language_id") String langId,
                                          @Field("club_id") String clubId,
                                          @Field("amount") String amount,
                                          @Field("coupon") String coupon,
                                          @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("payment_for_plan")
    Call<ResponseBody> planPayment(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("club_id") String clubId,
                                   @Field("plan_id") String planId,
                                   @Field("payment_method") String paymentMethod,
                                   @Field("user_ip") String userIp,
                                   @Field("order_ref") String orderRef,
                                   @Field("coupon_id") String couponId,
                                   @Field("discount") String discount);

    @FormUrlEncoded
    @POST("checking_phone_is_blocked")
    Call<ResponseBody> checkPhoneBlock(@Field("language_id") String langId,
                                       @Field("club_id") String clubId,
                                       @Field("owner_id") String ownerId,
                                       @Field("user_phone") String userPhone);

    @Multipart
    @POST("pending_balance_payment")
    Call<ResponseBody> removeBalance(@Part MultipartBody.Part photo,
                                     @Part("language_id") RequestBody langId,
                                     @Part("user_id") RequestBody userId,
                                     @Part("booking_id") RequestBody bookingId,
                                     @Part("balance_payment") RequestBody balancePayment,
                                     @Part("is_discount") RequestBody isDiscount);

    @FormUrlEncoded
    @POST("show_balance_in_earnings")
    Call<ResponseBody> earningBalanceDetail(@Field("language_id") String langId,
                                            @Field("user_id") String userId,
                                            @Field("club_id") String clubId,
                                            @Field("from") String from,
                                            @Field("to") String to);

    @FormUrlEncoded
    @POST("show_restriction_popup")
    Call<ResponseBody> showRestrictionPopup(@Field("language_id") String langId,
                                            @Field("user_id") String userId,
                                            @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("hide_restriction_popup")
    Call<ResponseBody> hideRestrictionPopup(@Field("language_id") String langId,
                                            @Field("user_id") String userId,
                                            @Field("popup_id") String popupId);

    @FormUrlEncoded
    @POST("dismiss_booking_target_popup")
    Call<ResponseBody> dismissLoyaltyPopup(@Field("language_id") String langId,
                                           @Field("user_id") String userId,
                                           @Field("popup_id") String popupId);

    @FormUrlEncoded
    @POST("add_employee_rating")
    Call<ResponseBody> addEmployeeRating(@Field("language_id") String langId,
                                         @Field("user_id") String userId,
                                         @Field("employee_id") String employeeId,
                                         @Field("club_id") String clubId,
                                         @Field("booking_id") String bookingId,
                                         @Field("rating") float rating,
                                         @Field("comments") String comments,
                                         @Field("amount") String amount,
                                         @Field("payment_method") String paymentMethod,
                                         @Field("order_ref") String orderRef,
                                         @Field("user_ip") String userIp);

    @FormUrlEncoded
    @POST("get_employee_ratings")
    Call<ResponseBody> getEmployeeRatings(@Field("language_id") String langId,
                                          @Field("user_id") String userId,
                                          @Field("employee_id") String employeeId,
                                          @Field("from_date") String from_date,
                                          @Field("to_date") String to_date,
                                          @Field("rating") String rating);

    @FormUrlEncoded
    @POST("employee_tip_payments_list")
    Call<ResponseBody> getEmployeeTipPayment(@Field("language_id") String langId,
                                             @Field("user_id") String userId,
                                             @Field("employee_id") String employeeId,
                                             @Field("from_date") String from_date,
                                             @Field("to_date") String to_date);

    @FormUrlEncoded
    @POST("employee_tip_payment")
    Call<ResponseBody> employeeTipPayment(@Field("language_id") String langId,
                                          @Field("user_id") String userId,
                                          @Field("employee_id") String employeeId,
                                          @Field("amount") String amount);

    @Multipart
    @POST("new_inventory_item")
    Call<ResponseBody> addItem(@Part MultipartBody.Part filePart,
                               @Part("language_id") RequestBody langId,
                               @Part("user_id") RequestBody userId,
                               @Part("club_id") RequestBody clubId,
                               @Part("item_name") RequestBody itemName,
                               @Part("purchased_price") RequestBody purchasedPrice,
                               @Part("sale_price") RequestBody salePrice,
                               @Part("stock") RequestBody stock);

    @Multipart
    @POST("modify_inventory_item")
    Call<ResponseBody> updateItem(@Part MultipartBody.Part filePart,
                                  @Part("language_id") RequestBody langId,
                                  @Part("user_id") RequestBody userId,
                                  @Part("club_id") RequestBody clubId,
                                  @Part("item_name") RequestBody itemName,
                                  @Part("purchased_price") RequestBody purchasedPrice,
                                  @Part("sale_price") RequestBody salePrice,
                                  @Part("stock") RequestBody stock,
                                  @Part("item_id") RequestBody itemId);

    @FormUrlEncoded
    @POST("get_inventory_items")
    Call<ResponseBody> getInventory(@Field("language_id") String langId,
                                    @Field("user_id") String userId,
                                    @Field("club_id") String clubId);

    @Multipart
    @POST("new_sale_item")
    Call<ResponseBody> newSaleItem(@Part MultipartBody.Part filePart,
                                   @Part("language_id") RequestBody langId,
                                   @Part("user_id") RequestBody userId,
                                   @Part("discount") RequestBody discount,
                                   @Part("employee_id") RequestBody employeeId,
                                   @Part("payment_method") RequestBody paymentMethod,
                                   @Part("note") RequestBody note,
                                   @Part("club_id") RequestBody clubId,
                                   @Part("items_data") RequestBody itemsData);

    @FormUrlEncoded
    @POST("get_stock_history")
    Call<ResponseBody> getStockHistory(@Field("language_id") String langId,
                                       @Field("user_id") String userId,
                                       @Field("item_id") String itemId);

    @FormUrlEncoded
    @POST("get_purchase_history")
    Call<ResponseBody> getPurchaseHistory(@Field("language_id") String langId,
                                          @Field("user_id") String userId,
                                          @Field("club_id") String clubId,
                                          @Field("from_date") String fromDate,
                                          @Field("to_date") String toDate,
                                          @Field("search") String search);

    @FormUrlEncoded
    @POST("get_order_items")
    Call<ResponseBody> getOrderItems(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("club_id") String clubId,
                                     @Field("from_date") String fromDate,
                                     @Field("to_date") String toDate,
                                     @Field("search") String search);

    @FormUrlEncoded
    @POST("get_item_order_details")
    Call<ResponseBody> getInventoryOrderDetail(@Field("language_id") String langId,
                                               @Field("user_id") String userId,
                                               @Field("order_id") String orderId);

    @FormUrlEncoded
    @POST("get_stock_report")
    Call<ResponseBody> getStockReport(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("club_id") String clubId);

    @FormUrlEncoded
    @POST("updating_item_stock")
    Call<ResponseBody> updateStock(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("club_id") String clubId,
                                   @Field("item_id") String itemId,
                                   @Field("stock") String stock);

    @FormUrlEncoded
    @POST("get_sale_report")
    Call<ResponseBody> getSaleReport(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("club_id") String clubId,
                                     @Field("from_date") String fromDate,
                                     @Field("to_date") String toDate);

    @FormUrlEncoded
    @POST("get_profit_report")
    Call<ResponseBody> getProfitReport(@Field("language_id") String langId,
                                       @Field("user_id") String userId,
                                       @Field("club_id") String clubId,
                                       @Field("from_date") String fromDate,
                                       @Field("to_date") String toDate);

    @FormUrlEncoded
    @POST("check_match_win_popup")
    Call<ResponseBody> showWinMatchPopup(@Field("language_id") String langId,
                                         @Field("user_id") String userId,
                                         @Field("booking_id") String bookingId,
                                         @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("dismiss_match_win_popup")
    Call<ResponseBody> dismissWinMatch(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                       @Field("dismiss_id") String dismissId);

    @FormUrlEncoded
    @POST("check_rating_and_tip_popup")
    Call<ResponseBody> showEmpRatePopup(@Field("language_id") String langId,
                                       @Field("user_id") String userId,
                                        @Field("app_module") String appModule);

    @FormUrlEncoded
    @POST("get_earning_with_inventory")
    Call<ResponseBody> getEarningInventory(@Field("language_id") String langId,
                                          @Field("user_id") String userId,
                                           @Field("club_id") String clubId,
                                           @Field("from") String from);

    @Multipart
    @POST("add_deposit_slip")
    Call<ResponseBody> addDepositSlip(@Part MultipartBody.Part filePart,
                                      @Part("language_id") RequestBody langId,
                                      @Part("user_id") RequestBody userId,
                                      @Part("club_id") RequestBody clubId,
                                      @Part("bank_id") RequestBody bankId,
                                      @Part("remaining_cash") RequestBody remainingCash,
                                      @Part("note") RequestBody note,
                                      @Part("stadium_cash") RequestBody stadiumCash,
                                      @Part("inventory_cash") RequestBody inventoryCash,
                                      @Part("deposited_amount") RequestBody depositedAmount,
                                      @Part("receipt_date") RequestBody receiptDate);

    @FormUrlEncoded
    @POST("/club/deposit_report_by_date")
    Call<ResponseBody> depositReportByDate(@Field("language_id") String langId,
                                           @Field("date") String date,
                                           @Field("club_id") String clubId);

    @Multipart
    @POST("/club/add_deposit_report")
    Call<ResponseBody> addDepositReport(@Part MultipartBody.Part file,
                                      @Part("language_id") RequestBody langId,
                                      @Part("club_id") RequestBody clubId,
                                        @Part("date") RequestBody date,
                                      @Part("note") RequestBody note);

    @Multipart
    @POST("/club/update_deposit_report")
    Call<ResponseBody> updateDepositReport(@Part MultipartBody.Part file,
                                        @Part("language_id") RequestBody langId,
                                        @Part("club_id") RequestBody clubId,
                                        @Part("file_id") RequestBody fileId,
                                        @Part("note") RequestBody note);







    @FormUrlEncoded
    @POST("get_expense_list")
    Call<ResponseBody> getExpenses(@Field("language_id") String langId,
                                   @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("delete_club_expense")
    Call<ResponseBody> deleteExpense(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("expense_id") String expenseId);

    @Multipart
    @POST("add_club_expense")
    Call<ResponseBody> addExpense(@Part MultipartBody.Part filePart,
                                  @Part("language_id") RequestBody langId,
                                  @Part("user_id") RequestBody userId,
                                  @Part("club_id") RequestBody clubId,
                                  @Part("bank_id") RequestBody bankId,
                                  @Part("expense_id") RequestBody expenseId,
                                  @Part("amount") RequestBody amount,
                                  @Part("expense_date") RequestBody date);

    @FormUrlEncoded
    @POST("check_player_outstandings")
    Call<ResponseBody> playerBalance(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("club_id") String clubId,
                                     @Field("player_id") String playerId);

    @FormUrlEncoded
    @POST("slots_for_update_time")
    Call<ResponseBody> getSlotsForChangeTime(@Field("language_id") String langId,
                                            @Field("user_id") String userId,
                                             @Field("booking_id") String bookingId,
                                             @Field("duration") String duration);

    @FormUrlEncoded
    @POST("save_updated_time")
    Call<ResponseBody> updateBookingTime(@Field("language_id") String langId,
                                         @Field("user_id") String userId,
                                         @Field("booking_id") String bookingId,
                                         @Field("time_start") String timeStart,
                                         @Field("time_end") String timeEnd,
                                         @Field("duration") String duration,
                                         @Field("shift") String shift);


    @Multipart
    @POST
    Call<ResponseBody> cutFace(@Url String url,
                               @Part MultipartBody.Part photo,
                               @Part("crop") RequestBody crop,
                               @Part("preview") RequestBody preview);

///////////////////////LINEUP//////////////////////////



    @FormUrlEncoded
    @POST("/lineup/v1/phone_book_players")
    Call<ResponseBody> phoneBookPlayer(@Field("language_id") String langId,
                                       @Field("numbers") String numbers);

//    @FormUrlEncoded
//    @POST("lineup/v1/lineup_bibs")
//    Call<ResponseBody> getBibs(@Field("language_id") String langId,
//                               @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("lineup/v1/get_unread_count")
    Call<ResponseBody> unreadNotifCount(@Field("language_id") String langId,
                                        @Field("user_id") String userId);


    @FormUrlEncoded
    @POST("lineup/v1/lineup_teams_data")
    Call<ResponseBody> teamsData(@Field("language_id") String langId,
                                 @Field("user_id") String userId,
                                 @Field("device_type") String deviceType);


    @FormUrlEncoded
    @POST("lineup/v1/save_app_settings")
    Call<ResponseBody> lineupSettings(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                      @Field("target_id") String targetId,
                                      @Field("type") String type);

    @FormUrlEncoded
    @POST("lineup/v1/get_friends")
    Call<ResponseBody> getFriends(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("search") String search);

    @FormUrlEncoded
    @POST("lineup/v1/add_best_player")
    Call<ResponseBody> addBestPlayer(@Field("language_id") String langId,
                                    @Field("user_id") String userId,
                                     @Field("game_id") String gameId,
                                     @Field("friend_id") String friendId);

    @FormUrlEncoded
    @POST("lineup/v1/link_with_ole")
    Call<ResponseBody> linkPlayers(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("message") String message,
                                   @Field("friend_ids") String friendIds);

    @Multipart
    @POST("lineup/v1/add_friend")
    Call<ResponseBody> addFriend(@Part MultipartBody.Part photo,
                                 @Part("language_id") RequestBody langId,
                                 @Part("user_id") RequestBody userId,
                                 @Part("nick_name") RequestBody nickName,
                                 @Part("bib_id") RequestBody userBib,
                                 @Part("country_code") RequestBody code,
                                 @Part("phone") RequestBody phone);

    @Multipart
    @POST("lineup/v1/update_friend")
    Call<ResponseBody> updateFriend(@Part MultipartBody.Part photo,
                                    @Part("language_id") RequestBody langId,
                                    @Part("user_id") RequestBody userId,
                                    @Part("friend_id") RequestBody friendId,
                                    @Part("nick_name") RequestBody nickName,
                                    @Part("bib_id") RequestBody userBib,
                                    @Part("country_code") RequestBody code,
                                    @Part("phone") RequestBody phone,
                                    @Part("friendship_id") RequestBody friendShipId,
                                    @Part("email") RequestBody email);

    @FormUrlEncoded
    @POST("lineup/v1/delete_friend")
    Call<ResponseBody> deletePlayer(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                    @Field("friend_id") String friendId,
                                    @Field("friendship_id") String friendShipId);

    @FormUrlEncoded
    @POST("lineup/v1/default_teams")
    Call<ResponseBody> defaultTeams(@Field("language_id") String langId,
                                   @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("lineup/v1/create_fast_lineup")
    Call<ResponseBody> fastLineup(@Field("language_id") String langId,
                                    @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("lineup/v1/update_team_and_game")
    Call<ResponseBody> updateGame(@Field("language_id") String langId,
                                 @Field("user_id") String userId,
                                  @Field("team_a_id") String teamAid,
                                  @Field("team_a_name") String teamAName,
                                  @Field("team_b_id") String teamBid,
                                  @Field("team_b_name") String teamBName,
                                  @Field("game_id") String gameId,
                                  @Field("game_date") String gameDate,
                                  @Field("game_time") String gameTime,
                                  @Field("players") String players,
                                  @Field("club_name") String clubName,
                                  @Field("city_name") String cityName);

    @FormUrlEncoded
    @POST("lineup/v1/create_game")
    Call<ResponseBody> createGame(@Field("language_id") String langId,
                                 @Field("user_id") String userId,
                                  @Field("game_date") String date,
                                  @Field("game_time") String time,
                                  @Field("players") String players,
                                  @Field("club_name") String clubName,
                                  @Field("city_name") String cityName);

    @FormUrlEncoded
    @POST("lineup/v1/add_to_game")
    Call<ResponseBody> addToGame(@Field("language_id") String langId,
                                 @Field("user_id") String userId,
                                 @Field("friend_ids") String ids,
                                 @Field("friendship_id") String friendShipIds,
                                 @Field("game_id") String gameId);

    @FormUrlEncoded
    @POST("lineup/v1/notify_for_game")
    Call<ResponseBody> notifyGame(@Field("language_id") String langId,
                                 @Field("user_id") String userId,
                                 @Field("friends") String ids,
                                 @Field("game_id") String gameId);

    @FormUrlEncoded
    @POST("lineup/v1/update_team_and_game")
    Call<ResponseBody> updateTeamShirt(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                       @Field("team_a_id") String teamAid,
                                       @Field("team_a_shirt") String teamAShirt,
                                       @Field("team_a_gk_shirt") String teamAgkShirt,
                                       @Field("team_b_id") String teamBid,
                                       @Field("team_b_shirt") String teamBShirt,
                                       @Field("team_b_gk_shirt") String teamBgkShirt);

    @FormUrlEncoded
    @POST("lineup/v1/save_coordinates")
    Call<ResponseBody> saveCoordinate(@Field("language_id") String langId,
                                    @Field("user_id") String userId,
                                      @Field("team_id") String teamId,
                                      @Field("game_id") String gameId,
                                      @Field("friend_id") String friendId,
                                      @Field("x_coordinate") float xCoordinate,
                                      @Field("y_coordinate") float yCoordinate,
                                      @Field("is_goalkeeper") String isGoalkeeper);

    @FormUrlEncoded
    @POST("lineup/v1/reset_game")
    Call<ResponseBody> resetGame(@Field("language_id") String langId,
                                 @Field("user_id") String userId,
                                 @Field("game_id") String gameId);

    @FormUrlEncoded
    @POST("lineup/v1/notify_game_ended")
    Call<ResponseBody> notifyGameEnd(@Field("language_id") String langId,
                                @Field("user_id") String userId,
                                 @Field("game_id") String gameId);

    @FormUrlEncoded
    @POST("lineup/v1/substitute_players")
    Call<ResponseBody> substitutePlayer(@Field("language_id") String langId,
                                       @Field("user_id") String userId,
                                        @Field("game_id") String gameId,
                                        @Field("old_player") String oldPlayer,
                                        @Field("new_player") String newPlayer,
                                        @Field("team_id") String teamId);

    @FormUrlEncoded
    @POST("lineup/v1/add_score")
    Call<ResponseBody> addScore(@Field("language_id") String langId,
                                @Field("user_id") String userId,
                                @Field("game_id") String gameId,
                                @Field("winner_team") String winnerTeam,
                                @Field("looser_team") String looserTeam,
                                @Field("is_draw") String isDraw,
                                @Field("is_canceled") String isCanceled);

    @FormUrlEncoded
    @POST("lineup/v1/remove_from_team")
    Call<ResponseBody> removeFromTeam(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                      @Field("team_id") String teamId,
                                      @Field("player_id") String playerId);

    @FormUrlEncoded
    @POST("lineup/v1/lineup_players_list")
    Call<ResponseBody> lineupPlayersList(@Field("language_id") String langId,
                                         @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("lineup/v1/compare_two_players")
    Call<ResponseBody> compareTwoPlayers(@Field("language_id") String langId,
                                         @Field("user_id") String userId,
                                         @Field("player_one") String playerOne,
                                         @Field("player_two") String playerTwo);

//    @FormUrlEncoded
//    @POST("lineup/v1/user_profile")
//    Call<ResponseBody> userProfile(@Field("language_id") String langId,
//                                  @Field("user_id") String userId,
//                                   @Field("loggedin_user") String loggedin_user);

    @FormUrlEncoded
    @POST("v2/get_profile")
    Call<ResponseBody> getUserProfile(@Field("language_id") String langId,
                                      @Field("player_id") String playerId,
                                      @Field("friendship_id") String friendShipIds,
                                      @Field("app_module") String appModule);



    @FormUrlEncoded
    @POST("lineup/v1/add_other_friend_to_my_list")
    Call<ResponseBody> addOtherFriendsToMyList(@Field("language_id") String langId,
                                               @Field("user_id") String userId,
                                               @Field("friend_id") String friendId);


    @FormUrlEncoded
    @POST("lineup/v1/mark_all_read")
    Call<ResponseBody> readAllNotification(@Field("language_id") String langId,
                                           @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("lineup/v1/delete_all_notifications")
    Call<ResponseBody> deleteAllNotification(@Field("language_id") String langId,
                                             @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("lineup/v1/get_notifications")
    Call<ResponseBody> notificationList(@Field("language_id") String langId,
                                       @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("lineup/v1/get_friends_in_team")
    Call<ResponseBody> friendsInGame(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("game_id") String gameId);

    @FormUrlEncoded
    @POST("lineup/v1/rate_game")
    Call<ResponseBody> addPlayerRating(@Field("language_id") String langId,
                                       @Field("user_id") String userId,
                                       @Field("friend_id") String friendId,
                                       @Field("friendship_id") String friendShipIds,
                                       @Field("game_id") String gameId,
                                       @Field("reach_time") String reachTime,
                                       @Field("comments") String comments,
                                       @Field("speed") int speed,
                                       @Field("shooting") int shooting,
                                       @Field("dribble") int dribble,
                                       @Field("iq_level") int iqLevel,
                                       @Field("fitness") int fitness,
                                       @Field("defence") int defence,
                                       @Field("played_level") int skills,
                                       @Field("got_card") String gotCard);

    @FormUrlEncoded
    @POST("lineup/v1/ratings_list")
    Call<ResponseBody> ratingsList(@Field("language_id") String langId,
                                   @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("lineup/v1/get_my_groups")
    Call<ResponseBody> getMyGroups(@Field("language_id") String langId,
                                  @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("lineup/v1/formation_details")
    Call<ResponseBody> formationDetails(@Field("language_id") String langId,
                                        @Field("user_id") String userId,
                                        @Field("game_id") String gameId,
                                        @Field("team_a_id") String team_a_id,
                                        @Field("team_a_status") String team_a_status,
                                        @Field("team_b_id") String team_b_id,
                                        @Field("team_b_status") String team_b_status);

    @FormUrlEncoded
    @POST("lineup/v1/my_games_history")
    Call<ResponseBody> gameHistoryLineup(@Field("language_id") String langId,
                                   @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("lineup/v1/get_game_players")
    Call<ResponseBody> gamePlayers(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("game_id") String gameId);

    @FormUrlEncoded
    @POST("lineup/v1/add_teams_captain")
    Call<ResponseBody> addTeamsCaptain(@Field("language_id") String langId,
                                      @Field("user_id") String userId,
                                       @Field("game_id") String gameId,
                                       @Field("team_a_id") String teamAId,
                                       @Field("team_b_id") String teamBId,
                                       @Field("team_a_captain_id") String teamACaptainId,
                                       @Field("team_b_captain_id") String teamBCaptainId);

    @FormUrlEncoded
    @POST("lineup/v1/remove_teams_captain")
    Call<ResponseBody> removeCaptain(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("game_id") String gameId,
                                     @Field("team_id") String teamId,
                                     @Field("captain_id") String captainId);

    @FormUrlEncoded
    @POST("lineup/v1/add_lineup_user_as_friend")
    Call<ResponseBody> addLineupUser(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                     @Field("friend_id") String friendId,
                                     @Field("name") String name,
                                     @Field("nick_name") String nickName,
                                     @Field("phone") String phone,
                                     @Field("bib_id") String bibId,
                                     @Field("is_link") String isLink,
                                     @Field("emoji_name") String emojiName);

    @FormUrlEncoded
    @POST("lineup/v1/remove_photo")
    Call<ResponseBody> removePhoto(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                   @Field("friendship_id") String friendShipIds);

    @FormUrlEncoded
    @POST("lineup/v1/remove_from_game")
    Call<ResponseBody> removeFromGame(@Field("language_id") String langId,
                                     @Field("user_id") String userId,
                                      @Field("player_id") String playerId,
                                      @Field("game_id") String gameId);

    @FormUrlEncoded
    @POST("lineup/v1/manual_player_link_request")
    Call<ResponseBody> linkPlayer(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("friend_id") String friendId,
                                  @Field("phone") String phone,
                                  @Field("country_code") String countryCode);

    @FormUrlEncoded
    @POST("lineup/v1/check_match_result_popup")
    Call<ResponseBody> showWinMatchPopup(@Field("language_id") String langId,
                                        @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("lineup/v1/check_captain_popup")
    Call<ResponseBody> showCaptainPopup(@Field("language_id") String langId,
                                        @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("lineup/v1/dismiss_popup")
    Call<ResponseBody> dismissPopup(@Field("language_id") String langId,
                                   @Field("user_id") String userId,
                                    @Field("popup_id") String popupId);

    @FormUrlEncoded
    @POST("lineup/v1/update_friend")
    Call<ResponseBody> playerStatus(@Field("language_id") String langId,
                                    @Field("user_id") String userId,
                                    @Field("friend_id") String friendId,
                                    @Field("friendship_id") String friendShipIds,
                                    @Field("player_status") String playerStatus);

    @FormUrlEncoded
    @POST("lineup/v1/manual_phone_search")
    Call<ResponseBody> manualPhoneSearch(@Field("language_id") String langId,
                                         @Field("user_id") String userId,
                                         @Field("friend_id") String friendId,
                                         @Field("phone") String phone,
                                         @Field("country_code") String countryCode);

    @FormUrlEncoded
    @POST("lineup/v1/swap_players_in_teams")
    Call<ResponseBody> swapPlayer(@Field("language_id") String langId,
                                  @Field("user_id") String userId,
                                  @Field("team_a_id") String team_a_id,
                                  @Field("player_a_id") String player_a_id,
                                  @Field("team_b_id") String team_b_id,
                                  @Field("player_b_id") String player_b_id);

    @Multipart
    @POST("lineup/v1/suggest_tshirt")
    Call<ResponseBody> suggestJersey(@Part MultipartBody.Part part,
                                     @Part("language_id") RequestBody langId,
                                    @Part("user_id") RequestBody userId,
                                     @Part("shirt_name") RequestBody name,
                                     @Part("shirt_country") RequestBody country,
                                     @Part("shirt_year") RequestBody year);

//    @FormUrlEncoded
//    @POST("lineup/v1/my_friends_groups")
//    Call<ResponseBody> getGroups(@Field("language_id") String langId,
//                                 @Field("user_id") String userId);

//    @FormUrlEncoded
//    @POST("lineup/v1/my_friend_friends")
//    Call<ResponseBody> getFriendofFriends(@Field("language_id") String langId,
//                                          @Field("user_id") String userId,
//                                          @Field("friend_id") String friendID);

    @FormUrlEncoded
    @POST("v2/disable_or_remove_account")
    Call<ResponseBody> disableUserAccountApi(@Field("language_id") String langId,
                                          @Field("user_id") String userId);

//    @FormUrlEncoded
//    @POST("/v2/get_ip_details")  //PHP
//    Call<ResponseBody> getIpDetails(@Field("user_ip") String userIp,
//                                     @Field("language_id") String langId);

    @FormUrlEncoded
    @POST("lineup/subscription/save")  //pass
    Call<ResponseBody> checkUserSubscription(@Field("language_id") String langId,
                                             @Field("subscriptionid") String subscriptionId,
                                             @Field("purchasetoken") String purchaseToken,
                                             @Field("packagename") String packageName,
                                             @Field("subscription_for") String subscriptionFor);


    @FormUrlEncoded
    @POST("/lineup/shirt/subscribed")
    Call<ResponseBody> unlockOneJersey(@Field("language_id") String langId,
                                       @Field("shirt_id") String shirtId);


    @FormUrlEncoded
    @POST("/lineup/v1/get_friend_requests")
    Call<ResponseBody> getFriendRequests(@Field("language_id") String langId);

    @FormUrlEncoded
    @POST("/lineup/v1/accept_decline_request")
    Call<ResponseBody> acceptDeclineRequest(@Field("language_id") String langId,
                                            @Field("request_id") String requestId,
                                            @Field("type") String type);





    //Ole Finance APIS

    @FormUrlEncoded
    @POST("/finance/home")
    Call<ResponseBody> financeHome(@Field("language_id") String langId,
                                      @Field("club_id") String clubId);
    @FormUrlEncoded
    @POST("/finance/get_club_banks")
    Call<ResponseBody> getClubBanksList(@Field("language_id") String langId,
                                      @Field("club_id") String clubId,
                                      @Field("bank_id") String bankId);



    //Income APIS

    @FormUrlEncoded
    @POST("/finance/get_income_history")
    Call<ResponseBody> incomeHistory(@Field("language_id") String langId,
                                      @Field("club_id") String clubId,
                                      @Field("filter_by") String filterBy,
                                      @Field("bank_id") String bankId,
                                      @Field("from") String from,
                                      @Field("to") String to);

    @FormUrlEncoded
    @POST("/finance/bank_deposits_report")
    Call<ResponseBody> bankDepositReport(@Field("language_id") String langId,
                                     @Field("club_id") String clubId,
                                     @Field("filter_by") String filterBy,
                                     @Field("bank_id") String bankId,
                                     @Field("from") String from,
                                     @Field("to") String to);



    @FormUrlEncoded
    @POST("/finance/income_details")
    Call<ResponseBody> incomeDetails(@Field("language_id") String langId,
                                        @Field("income_id") String incomeId);

    @Multipart
    @POST("/finance/update_income")
    Call<ResponseBody> updateIncome(@Part MultipartBody.Part file,
                                        @Part("language_id") RequestBody langId,
                                        @Part("income_id") RequestBody incomeId,
                                        @Part("club_id") RequestBody clubId,
                                        @Part("bank_id") RequestBody bankId,
                                        @Part("source") RequestBody source,
                                        @Part("amount") RequestBody amount,
                                        @Part("date") RequestBody date,
                                        @Part("income_type") RequestBody IncomeType,
                                        @Part("notes") RequestBody notes);

    @Multipart
    @POST("/finance/add_income")
    Call<ResponseBody> addIncome(@Part MultipartBody.Part file,
                                    @Part("language_id") RequestBody langId,
                                    @Part("club_id") RequestBody clubId,
                                    @Part("bank_id") RequestBody bankId,
                                    @Part("source") RequestBody source,
                                    @Part("amount") RequestBody amount,
                                    @Part("date") RequestBody date,
                                    @Part("income_type") RequestBody IncomeType,
                                    @Part("notes") RequestBody notes);


    //Expense APIS

    @FormUrlEncoded
    @POST("/finance/get_expenses_history")
    Call<ResponseBody> expenseHistory(@Field("language_id") String langId,
                                     @Field("club_id") String clubId,
                                      @Field("filter_by") String filterBy,
                                     @Field("bank_id") String bankId,
                                     @Field("expense_id") String expenseId,
                                     @Field("from") String from,
                                     @Field("to") String to);
    @FormUrlEncoded
    @POST("/finance/expense_details")
    Call<ResponseBody> expenseDetails(@Field("language_id") String langId,
                                      @Field("expense_id") String expenseId);

    @Multipart
    @POST("/finance/update_expense")
    Call<ResponseBody> updateExpense(@Part MultipartBody.Part file,
                                    @Part("language_id") RequestBody langId,
                                    @Part("record_id") RequestBody recordId,
                                    @Part("club_id") RequestBody clubId,
                                    @Part("expense_id") RequestBody expenseId,
                                    @Part("bank_id") RequestBody bankid,
                                    @Part("amount") RequestBody amount,
                                    @Part("notes") RequestBody notes);
    @Multipart
    @POST("/finance/add_expense")
    Call<ResponseBody> addFinanceExpense(@Part MultipartBody.Part file,
                                     @Part("language_id") RequestBody langId,
                                     @Part("club_id") RequestBody clubId,
                                     @Part("expense_id") RequestBody expenseId,
                                     @Part("bank_id") RequestBody bankid,
                                     @Part("amount") RequestBody amount,
                                     @Part("notes") RequestBody notes);



    @FormUrlEncoded
    @POST("/get_expense_list")
    Call<ResponseBody> getExpenseList(@Field("language_id") String langId);




    //Upcoming Expense APIS

    @FormUrlEncoded
    @POST("/finance/get_upcoming_expense_history")
    Call<ResponseBody> upcomingExpenseHistory(@Field("language_id") String langId,
                                      @Field("club_id") String clubId,
                                      @Field("bank_id") String bankId,
                                              @Field("filter_by") String filterBy,
                                      @Field("expense_id") String expenseId,
                                      @Field("from") String from,
                                      @Field("to") String to,
                                      @Field("upcoming_expense_id") String upcomingExpenseId);

    @Multipart
    @POST("/finance/update_upcoming_expense")
    Call<ResponseBody> updateUpcomingExpense(@Part MultipartBody.Part file,
                                     @Part("language_id") RequestBody langId,
                                     @Part("record_id") RequestBody recordId,
                                     @Part("club_id") RequestBody clubId,
                                     @Part("expense_id") RequestBody expenseId,
                                     @Part("bank_id") RequestBody bankid,
                                     @Part("amount") RequestBody amount,
                                     @Part("notes") RequestBody notes,
                                     @Part("recurring_type") RequestBody recurringType,
                                     @Part("recurring_date") RequestBody recurringDate);
    @Multipart
    @POST("/finance/add_upcoming_expense")
    Call<ResponseBody> addUpcomingExpense(@Part MultipartBody.Part file,
                                             @Part("language_id") RequestBody langId,
                                             @Part("club_id") RequestBody clubId,
                                             @Part("expense_id") RequestBody expenseId,
                                             @Part("bank_id") RequestBody bankid,
                                             @Part("amount") RequestBody amount,
                                             @Part("notes") RequestBody notes,
                                             @Part("recurring_type") RequestBody recurringType,
                                             @Part("recurring_date") RequestBody recurringDate);



    @Multipart
    @POST("/finance/pay_upcoming_expense")
    Call<ResponseBody> payUpcomingExpense(@Part MultipartBody.Part file,
                                             @Part("language_id") RequestBody langId,
                                             @Part("upcoming_expense_id") RequestBody upcomingExpenseId,
                                             @Part("club_id") RequestBody clubId);


    //Partner Apis

    @FormUrlEncoded
    @POST("/finance/partner_paid_history")
    Call<ResponseBody> partnerPaidHistory(@Field("language_id") String langId,
                                     @Field("club_id") String clubId,
                                     @Field("partner_id") String partnerId,
                                     @Field("filter_by") String filterBy,
                                     @Field("bank_id") String bankId,
                                     @Field("from") String from,
                                     @Field("to") String to);

    @FormUrlEncoded
    @POST("/finance/get_club_partners")
    Call<ResponseBody> getClubPartnersList(@Field("language_id") String langId,
                                           @Field("club_id") String clubId,
                                           @Field("partner_id") String partnerId);


    @Multipart
    @POST("/finance/add_partner")
    Call<ResponseBody> addPartner(@Part MultipartBody.Part file,
                               @Part("language_id") RequestBody langId,
                               @Part("club_id") RequestBody clubId,
                               @Part("name") RequestBody bankName,
                               @Part("email") RequestBody accountTitle,
                               @Part("phone") RequestBody accountNumber,
                               @Part("shares") RequestBody ibanNumber);


    @Multipart
    @POST("/finance/update_partner")
    Call<ResponseBody> updatePartner(@Part MultipartBody.Part file,
                                  @Part("language_id") RequestBody langId,
                                  @Part("club_id") RequestBody clubId,
                                  @Part("partner_id") RequestBody partnerId,
                                  @Part("name") RequestBody bankName,
                                  @Part("email") RequestBody accountTitle,
                                  @Part("phone") RequestBody accountNumber,
                                  @Part("shares") RequestBody ibanNumber);

    @FormUrlEncoded
    @POST("/finance/delete_partner")
    Call<ResponseBody> deletePartner(@Field("language_id") String langId,
                                  @Field("club_id") String clubId,
                                  @Field("partner_id") String partnerId);

    @Multipart
    @POST("/finance/pay_to_partner")
    Call<ResponseBody> payToPartner(@Part MultipartBody.Part file,
                                     @Part("language_id") RequestBody langId,
                                     @Part("club_id") RequestBody clubId,
                                     @Part("bank_id") RequestBody bankId,
                                     @Part("amount") RequestBody amount,
                                     @Part("month") RequestBody month,
                                     @Part("notes") RequestBody notes);


    //Employee Salary Apis

    @FormUrlEncoded
    @POST("/finance/employee_salary_history")
    Call<ResponseBody> employeeSalaryHistory(@Field("language_id") String langId,
                                          @Field("club_id") String clubId,
                                          @Field("employee_id") String employeeId,
                                          @Field("filter_by") String filterBy,
                                          @Field("from") String from,
                                          @Field("to") String to);


    @Multipart
    @POST("/finance/give_employee_salary")
    Call<ResponseBody> payEmployeeSalary(@Part MultipartBody.Part file,
                                         @Part("language_id") RequestBody langId,
                                         @Part("club_id") RequestBody clubId,
                                         @Part("bank_id") RequestBody bankId,
                                         @Part("employee_id") RequestBody employeeId,
                                         @Part("salary") RequestBody salary,
                                         @Part("month") RequestBody month,
                                         @Part("notes") RequestBody notes);




    //Bank Apis

    @Multipart
    @POST("/finance/add_bank")
    Call<ResponseBody> addBank(@Part MultipartBody.Part file,
                                            @Part("language_id") RequestBody langId,
                                            @Part("club_id") RequestBody clubId,
                                            @Part("bank_name") RequestBody bankName,
                                            @Part("account_title") RequestBody accountTitle,
                                            @Part("account_number") RequestBody accountNumber,
                                            @Part("iban_number") RequestBody ibanNumber,
                                            @Part("branch_name") RequestBody branchName,
                                            @Part("bank_city") RequestBody bankCity,
                                            @Part("deposit_type") RequestBody depositType);


    @Multipart
    @POST("/finance/update_bank")
    Call<ResponseBody> updateBank(@Part MultipartBody.Part file,
                               @Part("language_id") RequestBody langId,
                               @Part("club_id") RequestBody clubId,
                               @Part("bank_id") RequestBody bankId,
                               @Part("bank_name") RequestBody bankName,
                               @Part("account_title") RequestBody accountTitle,
                               @Part("account_number") RequestBody accountNumber,
                               @Part("iban_number") RequestBody ibanNumber,
                               @Part("branch_name") RequestBody branchName,
                               @Part("bank_city") RequestBody bankCity,
                               @Part("deposit_type") RequestBody depositType);


    @FormUrlEncoded
    @POST("/finance/delete_bank")
    Call<ResponseBody> deleteBank(@Field("language_id") String langId,
                                        @Field("club_id") String clubId,
                                        @Field("bank_id") String bankId);


    //Gifts Apis

    @FormUrlEncoded
    @POST("/gifts/get_gift_list")
    Call<ResponseBody> getGiftsList(@Field("language_id") String langId,
                                  @Field("club_id") String clubId,
                                  @Field("gift_id") String giftId);


    @Multipart
    @POST("/gifts/add_new_gift_target")
    Call<ResponseBody> addGift(@Part MultipartBody.Part file,
                                  @Part("language_id") RequestBody langId,
                                  @Part("club_id") RequestBody clubId,
                                  @Part("target_id") RequestBody targetId,
                                  @Part("name") RequestBody name,
                                  @Part("details") RequestBody details,
                                  @Part("start_date") RequestBody startDate,
                                  @Part("end_date") RequestBody endDate);

    @Multipart
    @POST("/gifts/update_gift_target")
    Call<ResponseBody> updateGift(@Part MultipartBody.Part file,
                               @Part("language_id") RequestBody langId,
                               @Part("gift_id") RequestBody giftId,
                               @Part("club_id") RequestBody clubId,
                               @Part("target_id") RequestBody targetId,
                               @Part("name") RequestBody name,
                               @Part("details") RequestBody details,
                               @Part("start_date") RequestBody startDate,
                               @Part("end_date") RequestBody endDate);


    @FormUrlEncoded
    @POST("/gifts/gift_target_list")
    Call<ResponseBody> getGiftTargetList(@Field("language_id") String langId);

    @FormUrlEncoded
    @POST("/gifts/delete_gift_target")
    Call<ResponseBody> deleteGift(@Field("language_id") String langId,
                                    @Field("club_id") String clubId,
                                    @Field("gift_id") String giftId);

    //Statistics API

    @FormUrlEncoded
    @POST("/club/stats")
    Call<ResponseBody> clubStatistics(@Field("language_id") String langId,
                                  @Field("club_id") String clubId,
                                  @Field("by_years") String byYears,
                                  @Field("by_months") String byMonths,
                                  @Field("by_days") String byDays,
                                  @Field("from") String from,
                                  @Field("to") String to);


    @FormUrlEncoded
    @POST("/club/stats_graph_data")
    Call<ResponseBody> statsGraphData(@Field("language_id") String langId,
                                      @Field("club_id") String clubId,
                                      @Field("filter_type") String filterType,
                                      @Field("by_years") String byYears,
                                      @Field("by_months") String byMonths,
                                      @Field("by_days") String byDays,
                                      @Field("from") String from,
                                      @Field("to") String to);


    //Document Folder Apis


    @FormUrlEncoded
    @POST("/assets/home")
    Call<ResponseBody> documentsHome(@Field("language_id") String langId,
                                  @Field("club_id") String clubId);




    @Multipart
    @POST("/assets/add_new_folder")
    Call<ResponseBody> addNewFolder(@Part MultipartBody.Part file,
                                  @Part("language_id") RequestBody langId,
                                  @Part("club_id") RequestBody clubId,
                                  @Part("name") RequestBody name);

    @Multipart
    @POST("/assets/update_folder")
    Call<ResponseBody> updateFolder(@Part MultipartBody.Part file,
                                    @Part("language_id") RequestBody langId,
                                    @Part("club_id") RequestBody clubId,
                                    @Part("name") RequestBody name,
                                    @Part("folder_id") RequestBody folderId);


    @FormUrlEncoded
    @POST("/assets/delete_folder")
    Call<ResponseBody> deleteFolder(@Field("language_id") String langId,
                                    @Field("club_id") String clubId,
                                    @Field("folder_id") String folderId);

    @FormUrlEncoded
    @POST("/assets/folder_details")
    Call<ResponseBody> folderDetails(@Field("language_id") String langId,
                                     @Field("club_id") String clubId,
                                     @Field("type") String type,
                                     @Field("type_id") String typeId);

    //Document Files Apis


    @FormUrlEncoded
    @POST("/assets/get_file")
    Call<ResponseBody> getFile(@Field("language_id") String langId,
                                    @Field("club_id") String clubId,
                                    @Field("file_id") String fileId);


    @Multipart
    @POST("/assets/add_new_file")
    Call<ResponseBody> addNewFile(@Part MultipartBody.Part file,
                                  @Part("language_id") RequestBody langId,
                                  @Part("club_id") RequestBody clubId,
                                  @Part("type") RequestBody type,
                                  @Part("type_id") RequestBody typeId,
                                  @Part("file_name") RequestBody fileName,
                                  @Part("file_number") RequestBody fileNumber,
                                  @Part("issued_date") RequestBody issuedDate,
                                  @Part("expiry_date") RequestBody expiryDate);




    @Multipart
    @POST("/assets/update_file")
    Call<ResponseBody> updateFile(@Part MultipartBody.Part file,
                                  @Part("language_id") RequestBody langId,
                                  @Part("club_id") RequestBody clubId,
                                  @Part("file_id") RequestBody fileId,
                                  @Part("file_name") RequestBody fileName,
                                  @Part("file_number") RequestBody fileNumber,
                                  @Part("issued_date") RequestBody issuedDate,
                                  @Part("expiry_date") RequestBody expiryDate);



    @FormUrlEncoded
    @POST("/assets/delete_file")
    Call<ResponseBody> deleteFile(@Field("language_id") String langId,
                                   @Field("club_id") String clubId,
                                   @Field("file_id") String fileId);


    //Node JS APIS

    @GET("player/startup")
    Call<ResponseBody> getIpDetails(@Query("user_ip") String userIp,
                                    @Query("language_id") String langId);
    @GET("player/lookups/goalkeeper-shirts")
    Call<ResponseBody> getGoalKeeperShirts();

    @GET("player/lookups/fields")
    Call<ResponseBody> getAllFields();

    @GET("player/lookups/chairs")
    Call<ResponseBody> getAllChairs();

    @GET("player/lookups/bibs")
    Call<ResponseBody> getBibs();

    @GET("player/lookups/countries")
    Call<ResponseBody> getAllCountries();

    @GET("player/my/lineup/shirt/details")
    Call<ResponseBody> getTeamAndShirtDetails(@Query("country_id") String countryId,
                                              @Query("device_type") String deviceType);


    @GET("player/my/lineup/friends/groups")
    Call<ResponseBody> getGroups();



//    @GET("player/my/lineup/formation/details")
//    Call<ResponseBody> getFormationDetails(@Query("game_id") String gameId);

    @GET("player/my/lineup/formation/details")
    Call<ResponseBody> getFormationDetails(@Query("language_id") String langId,
                                            @Query("user_id") String userId,
                                            @Query("game_id") String gameId,
                                            @Query("team_a_id") String team_a_id,
                                            @Query("team_a_status") String team_a_status,
                                            @Query("team_b_id") String team_b_id,
                                            @Query("team_b_status") String team_b_status);


    @GET("player/my/lineup/friends")
    Call<ResponseBody> getFriendsNodeApi(@Query("friend_id") String friendID);

    //Global Lineup Apis

    @FormUrlEncoded
    @POST("owner/employees/add-update-player-as-employee")
    Call<ResponseBody> addUpdatePlayerAsEmployee(@Field("player_id") String playerId,
                                                @Field("club_id") String clubId,
                                                @Field("assigned_country_ids[]") String[] assignedCountryId);

    @FormUrlEncoded
    @POST("owner/employees/remove-player-as-employee")
    Call<ResponseBody> removePlayerAsEmployee(@Field("player_id") String playerId);

    @GET("owner/employees/assigned-countries")
    Call<ResponseBody> getAssignedCountries();

    @GET("lineup/global/team-players")
    Call<ResponseBody> lineupGlobalTeamPlayers(@Query("team_id") String teamId);



    @FormUrlEncoded
    @POST("lineup/global/add-player")
    Call<ResponseBody> addGlobalLineupPlayer(@Field("name") String name,
                                             @Field("arabic_name") String arabicName,
                                             @Field("team_id") String teamId,
                                             @Field("country_id") String countryId,
                                             @Field("bib_id") String bibId,
                                             @Field("photo") String photo);

    @FormUrlEncoded
    @POST("lineup/global/update-player")
    Call<ResponseBody> updateGlobalLineupPlayer(@Field("name") String name,
                                                @Field("arabic_name") String arabicName,
                                                @Field("team_id") String teamId,
                                                @Field("country_id") String countryId,
                                                @Field("bib_id") String bibId,
                                                @Field("photo") String photo,
                                                @Field("player_id") String playerId);
    @FormUrlEncoded
    @POST("lineup/global/remove-player")
    Call<ResponseBody> removeGlobalLineupPlayer(@Field("player_id") String playerId);

    @Multipart
    @POST("save_player_photo") //pass
    Call<ResponseBody> savePlayerPhoto(@Part MultipartBody.Part file);

    //Real Lineup Apis


    @GET("lineup/global/countries")
    Call<ResponseBody> realLineupCountries();

    @GET("lineup/global/start-match")
    Call<ResponseBody> startRealLineup(@Query("team_id") String teamId);

    @FormUrlEncoded
    @POST("lineup/global/save-player-position")
    Call<ResponseBody> saveRealLineupPlayerPositions(@Field("match_id") String matchId,
                                                     @Field("player_id") String playerId,
                                                     @Field("x_cord") float xCordinate,
                                                     @Field("y_cord") float yCordinate,
                                                     @Field("is_goalkeeper") String isGoalkeeper);

    @FormUrlEncoded
    @POST("lineup/global/reset-match")
    Call<ResponseBody> resetRealLineup(@Field("match_id") String matchId);


    @FormUrlEncoded
    @POST("lineup/global/remove-player-position")
    Call<ResponseBody> removeRealLineupPlayer(@Field("match_id") String matchId,
                                              @Field("player_id") String playerId);

    @GET("player/my/lineup/selections")
    Call<ResponseBody> getLineupSelections();

    @FormUrlEncoded
    @POST("lineup/global/save-match-shirts")
    Call<ResponseBody> saveRealLineupMatchShirts(@Field("match_id") String matchId,
                                                @Field("team_shirt_id") String teamShirtId,
                                                @Field("goalkeeper_shirt_id") String goalkeeperShirtId);


    @FormUrlEncoded
    @POST("lineup/game/share")
    Call<ResponseBody> shareGameCount(@Field("game_id") String gameId,
                                      @Field("type") String type);















}
